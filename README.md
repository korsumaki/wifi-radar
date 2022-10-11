# Wifi Radar

WiFi Radar is (experimental) application to scan WiFi access points and create map about those.

### TODO list

Here is list of ideas what to do

- Functionality
  - distance calculation
  - map chart element on UI
  - Canvas: add automatic centering and scaling
  - more advanced methods for getting distance
    - https://developer.android.com/guide/topics/connectivity/wifi-rtt
    - https://github.com/Plinzen/android-rttmanager-sample
  - BT scanning
  - gather real scan data to be used in simulations
  - get step count for estimating distance between scans

- Application
  - remember list during orientation change
    - missing call to unregisterReceiver()
  - check app version number, change to use git tag version?
  - viewModel
  - App layout (Scaffold)
  - icon
  - support for different languages (en, fi)
  - enable crashlytics
  - enable leakcanary
  - publish to play store
  - compose testing?
    - https://developer.android.com/jetpack/compose/testing

- Project/github
  - project description
  - readme
