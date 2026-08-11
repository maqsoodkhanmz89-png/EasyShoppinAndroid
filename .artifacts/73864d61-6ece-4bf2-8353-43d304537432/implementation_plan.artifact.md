# Implementation Plan - Manage Address Feature

This plan outlines the steps to implement the Manage Address functionality in the EasyShopping Android app, following the provided backend API documentation.

## Proposed Changes

### Data Layer

#### [MODIFY] [Models.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/data/model/Models.kt)
- Add `Address` data class representing the backend address object.
- Add `AddressRequest` for POST/PUT operations.
- Add `AddressListResponse` and `AddressActionResponse` for API responses.

#### [MODIFY] [ApiService.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/data/api/ApiService.kt)
- Add `GET api/addresses`
- Add `POST api/addresses`
- Add `PUT api/addresses/{id}`
- Add `DELETE api/addresses/{id}`

### UI Layer

#### [NEW] [AddressViewModel.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/address/AddressViewModel.kt)
- Manage list of addresses.
- Handle state for loading, error, and success.
- Implement functions for fetching, adding, updating, and deleting addresses.

#### [NEW] [AddressListScreen.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/address/AddressListScreen.kt)
- Display a list of addresses in a clean, modern UI.
- Floating action button to add a new address.
- Options to edit or delete each address.
- Swipe-to-delete or action buttons.

#### [NEW] [AddEditAddressScreen.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/address/AddEditAddressScreen.kt)
- Form with validation for all required fields (Address Line 1, City, State, Country, Zip Code, Phone Number).
- Toggle for "Set as Default".
- Dropdown or RadioButtons for address type (Home, Office, Other).

### Navigation & Integration

#### [MODIFY] [MainActivity.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/MainActivity.kt)
- Add navigation routes for `address_list` and `add_edit_address`.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/home/HomeScreen.kt)
- Update the "Shipping Address" list item in `AccountScreen` to navigate to `address_list`.

## Verification Plan

### Automated Tests
- N/A (Manual verification on device/emulator)

### Manual Verification
1. Navigate to Account -> Shipping Address.
2. Verify list is fetched from backend (if any).
3. Click "+" to add a new address. Fill form and save.
4. Verify new address appears in the list.
5. Edit an existing address and verify changes.
6. Delete an address and verify it is removed.
7. Verify "Set as Default" logic works as expected.
