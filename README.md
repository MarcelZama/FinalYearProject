# Interactive Map of SETU Carlow![Screenshot_3](https://github.com/MarcelZama/FinalYearProject/assets/92083030/0a596aaf-5028-488b-a428-3acbb63891da)


## Project Overview

The Interactive Map of SETU Carlow is a dynamic, user-friendly application designed to assist students, staff, and visitors in navigating the South-East Technological University (SETU) Carlow campus. This project leverages modern web development tools and technologies to provide a seamless and interactive experience.

## Features

- **Campus Navigation**: Users can easily locate buildings, departments, and other facilities within the campus.
- **Search Functionality**: Quickly find specific locations or points of interest.
- **Responsive Design**: Accessible on various devices including desktops, tablets, and smartphones.
- **Real-time Updates**: The map can be updated in real-time to reflect any changes in the campus layout or new points of interest.
- **Integration with Firebase**: Utilizes Firebase for real-time database management and updates.

## Technologies Used

- **Java**: The core programming language used for application development.
- **Android Studio**: The primary IDE for developing the Android version of the map.
- **Firebase**: Used for real-time database functionalities.
- **JavaScript, HTML, CSS**: For the web version of the interactive map.
- **Google Maps API**: For integrating map functionalities and visualizations.

## Installation and Setup

### Prerequisites

- Java Development Kit (JDK)
- Android Studio
- Firebase Account
- Google Maps API Key

### Steps to Run the Project

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/marcelzama/interactive-map-setu-carlow.git
   cd interactive-map-setu-carlow

2. **Set Up Firebase**:
- Create a project in Firebase.
- Enable Firestore Database.
- Download the google-services.json file and place it in the app directory of your Android Studio project.

3. **Configure Google Maps API**:
- Obtain a Google Maps API key from the Google Cloud Console.
- Add the API key to your AndroidManifest.xml file:
  ```xml
  <meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE"/>

4. **Build and Run the Project**:
- Open the project in Android Studio.
- Sync the project with Gradle files.
- Build and run the application on an emulator or an Android device.

### Usage
1. **Launch the Application**:

- Open the app on your device.
- Allow location permissions if prompted.
  
2. **Explore the Campus**:

- Use the interactive map to navigate through the campus.
- Use the search bar to find specific buildings or locations.
  
3. **Real-time Updates**:

- The map will automatically update with any changes or new points of interest added through Firebase.

## Contribution

Contributions are welcome! If you would like to contribute to the project, please follow these steps:

1. **Fork the Repository**:

- Click the "Fork" button at the top of this repository.

2. **Create a Branch**:
   ```bash 
   git checkout -b feature/your-feature-name
3. **Make Your Changes**:
   
- Implement your feature or fix.
  
4. **Commit Your Changes**:
   ```bash
   git commit -m "Add feature: your feature name"

5. **Push to the Branch**:
   ```bash
   git push origin feature/your-feature-name

6. **Open a Pull Request**:

- Go to the repository in GitHub and click "New Pull Request".

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Contact
For any inquiries or feedback, please contact Marcel Zama at MarcelZama@outlook.com.

___________________________________________________________________________________________________

By following this README, you will have a clear understanding of how to set up, use, and contribute to the Interactive Map of SETU Carlow project. If you have any questions or need further assistance, feel free to reach out.
