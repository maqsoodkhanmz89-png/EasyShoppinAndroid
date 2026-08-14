# Implementation Plan - Add Mobile Number and Password Validation

This plan outlines the changes required to add a mandatory "Mobile Number" field to the Registration and Address sections, and to implement password matching validation during registration.

## Proposed Changes

### Data Models
#### [MODIFY] [Models.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/data/model/Models.kt)
- Update `RegisterRequest` to include `phoneNumber: String`.

### Registration Feature
#### [MODIFY] [CreateAccountViewModel.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/create/CreateAccountViewModel.kt)
- Add `mobileNumber` and `confirmPassword` to `CreateAccountUiState`.
- Add state update functions for the new fields.
- Update `onRegisterClicked` to:
    - Validate that Name, Email, Mobile Number, Password, and Confirm Password are not blank.
    - Verify that `password` and `confirmPassword` match.
    - Include `mobileNumber` in the `RegisterRequest`.

#### [MODIFY] [CreateAccountScreen.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/create/CreateAccountScreen.kt)
- Add a new `OutlinedTextField` for "Mobile Number".
- Update the existing "Password" field label to "Create Password".
- Add a new `OutlinedTextField` for "Confirm Password".
- Ensure all new fields are wired to the ViewModel.

### Address Feature
#### [MODIFY] [AddEditAddressScreen.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/address/AddEditAddressScreen.kt)
- Update the label for `phoneNumber` field to "Mobile Number" for clarity.
- Ensure the field is mandatory (already partially implemented in the "Save Address" button enable logic).

## Verification Plan

### Automated Tests
- N/A (Manual verification on device is preferred for UI/Flow changes).

### Manual Verification
1. **Registration Flow**:
    - Open the Registration screen.
    - Verify that "Full Name", "Email Address", "Mobile Number", "Create Password", and "Confirm Password" fields are visible.
    - Try to register with blank fields and verify the error message.
    - Try to register with mismatched passwords and verify the error message.
    - Complete registration with valid, matching data and verify success.
2. **Address Management**:
    - Open the "Add New Address" screen.
    - Verify the "Mobile Number" field is present.
    - Verify that the address cannot be saved if the mobile number is blank.
    - Add/Update an address and verify the mobile number is persisted.
