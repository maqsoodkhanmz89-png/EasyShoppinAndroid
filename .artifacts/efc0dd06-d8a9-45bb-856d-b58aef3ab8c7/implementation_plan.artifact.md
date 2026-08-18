# Implementation Plan - Fix Authentication & Address UI Synchronization

This plan addresses the intermittent "Invalid or expired token" errors and the issue where newly added addresses do not immediately appear in the UI.

## Root Cause Analysis
1.  **Authentication**: Manual passing of the token in every API call is prone to race conditions and inconsistent state. The "3-4 attempts" failure suggests that the token might not be fully persisted or retrieved correctly in the first few attempts after a session change.
2.  **UI Sync**: The Address List doesn't update immediately because of multiple competing `fetchAddresses()` calls and a lack of guaranteed sequencing between the `addAddress` success and the list refresh.

## Proposed Changes

### 1. Network Layer (Authentication)
#### [MODIFY] [RetrofitClient.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/data/api/RetrofitClient.kt)
- Implement a `HeaderInterceptor` to automatically add the `Authorization` header to all requests.
- Retrieve the latest token directly from `SessionManager` within the interceptor.
- This ensures that the *very latest* token is used for every single request, eliminating manual parameter errors.

#### [MODIFY] [ApiService.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/data/api/ApiService.kt)
- Remove all `@Header("Authorization") token: String` parameters from all interface methods.
- The `HeaderInterceptor` will now handle this globally.

### 2. Business Logic (ViewModel)
#### [MODIFY] [AddressViewModel.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/address/AddressViewModel.kt)
- Remove manual token retrieval and passing logic.
- Ensure `addAddress` and `updateAddress` wait for the subsequent `fetchAddresses` to complete before triggering the `onSuccess` callback. This guarantees that the "back stack" return will see a fully updated state.

### 3. User Interface
#### [MODIFY] [AddressListScreen.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/address/AddressListScreen.kt)
- Simplify the `LaunchedEffect` to only fetch data once when the screen is first created, as the `ViewModel` will now handle proactive refreshes after mutations.

## Verification Plan
1. **Authentication Check**: Log in and immediately try to add an address. Verify it works on the **first attempt** (no 401).
2. **UI Sync Check**: Add a new address and verify it appears in the list the moment the screen closes and returns to the list.
3. **Regression Check**: Verify that the Cart, Checkout, and Order history still work correctly with the new global authentication interceptor.
