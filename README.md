# Azkar App

An Islamic Android app to help Muslims keep up with daily Azkar, track worship, and stay consistent.

## Features

- **Azkar Counter** - Morning, Evening, Sleep & Wake-up supplications with tap counter and save
- **Kanz** - Virtues tracker with scoring system (x10, x20, x50, x100) and progress chart
- **Missed Prayers** - Calculate and track missed prayers (Fajr, Dhuhr, Asr, Maghrib, Isha)
- **Fasting Tracker** - Log fasting days
- **Dawa Lists** - Islamic sermon playlists linked to YouTube
- **Prayer Times** - Real-time prayer times via Aladhan API
- **Dark / Light mode**
- **Cloudinary** for image uploads

## Tech Stack

- **Kotlin** | **XML** | **Material Design 3**
- **MVVM** - ViewModel + LiveData + Coroutines
- **Room** - Local database
- **Firebase** - Firestore, Storage
- **Retrofit** - Networking
- **Cloudinary** - Image uploads
- **DataStore** - Preferences
- Min SDK 24 | Target SDK 36

## Setup

1. Clone the repo
   ```bash
   git clone https://github.com/YOUR_USERNAME/AzkarApp.git
   ```
2. Add your `google-services.json` in the `app/` directory
3. Open in Android Studio, sync Gradle, and run

## License

This project is open source and available under the [MIT License](LICENSE).
