# Support for Multiple Product Images with Carousel Navigation

Integrate backend support for multiple product images in the Product Details screen using a carousel with dot navigation, matching the official app's look and feel.

## Proposed Changes

### Data Model & API

#### [MODIFY] [Models.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/data/model/Models.kt)
- Update `Product` data class to include `images: List<String>`.
- Add `SingleProductResponse` to handle fetching a single product's details.

#### [MODIFY] [ApiService.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/data/api/ApiService.kt)
- Add `getProductDetails(@Path("id") id: Int): Response<SingleProductResponse>` endpoint.

#### [MODIFY] [ProductDetailsViewModel.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/details/ProductDetailsViewModel.kt)
- Update `fetchProductDetails` to use the new `getProductDetails` API instead of filtering the list of all products.

### User Interface

#### [MODIFY] [ProductDetailsScreen.kt](file:///C:/Users/MKhanMohammedZai/Downloads/Project/Android/EasyShoppinAndroid/EasyShoppinAndroid/app/src/main/java/com/mahad/easyshopping/ui/details/ProductDetailsScreen.kt)
- Replace the single `AsyncImage` in the product details with a `HorizontalPager`.
- Implement a dot indicator (pager indicator) for image navigation.
- Ensure image URLs are correctly handled (prepended with `BASE_URL` if they are relative paths).

## Verification Plan

### Automated Tests
- Build the project to ensure data model changes don't break existing features.

### Manual Verification
- Deploy to the device.
- Navigate to a product with multiple images (e.g., ID 11 "New Fortuner").
- Verify that the image carousel displays all images.
- Verify that the dot navigation updates correctly as images are swiped.
- Verify that products with a single image still display correctly.
