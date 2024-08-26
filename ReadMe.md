# Hospital Locator & Ambulance Booking App

This Android application helps users locate the nearest hospital using their current GPS location and provides an option to book an ambulance.

## Features

- **GPS Location**: Automatically detects the user's current location.
- **Nearest Hospital Search**: Uses the Google Places API to find hospitals within a 5km radius.
- **Hospital Details**: Displays the name, address, and distance of the nearest hospital.
- **Ambulance Booking**: Provides a button to simulate the booking of an ambulance for the selected hospital.

## Technologies Used

- **Android Studio**
- **Google Maps API**
- **Google Places API**
- **Retrofit** for networking

## Getting Started

### Prerequisites

- Android Studio installed on your computer.
- A Google Cloud account with an active API key for Google Maps and Places APIs.

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Ritvik-Girish/hospital-locator.git
   cd Ambulocate

2.Open the project in Android Studio:
Go to File > Open... and select the hospital-locator directory.

3.Set up your Google Maps API Key:
In app/src/main/res/values/strings.xml, replace YOUR_GOOGLE_MAPS_API_KEY_HERE with your actual API key:
xml
Copy code
<string name="google_maps_key">YOUR_GOOGLE_MAPS_API_KEY_HERE</string>

4.Build and Run:
Connect your Android device or use an emulator.
Click on the Run button in Android Studio to build and run the app.
Usage
Start the App: The app will automatically try to find your current location.
Locate Nearest Hospital: The map will display your location and the nearest hospital within a 5km radius.
View Hospital Details: The name, address, and distance to the nearest hospital will be shown.
Book Ambulance: Click on the "Book Ambulance" button to simulate booking an ambulance.

5.Project Structure
MainActivity.java: Contains the main logic for the map, location services, and UI interactions.
GooglePlacesService.java: Interface for Retrofit to communicate with the Google Places API.
Place.java & PlacesResponse.java: Models to parse the JSON responses from the Google Places API.
activity_main.xml: Layout file defining the UI of the main screen.

6.Dependencies
Google Play Services: Required for using Google Maps and location services.
Retrofit: A type-safe HTTP client for Android and Java.

7.License
This project is licensed under the MIT License.
