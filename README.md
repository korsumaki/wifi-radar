# Wifi Radar

WiFi Radar is (experimental) application to scan WiFi access points and create map about those.

Other purpose for this application is to learn more Kotlin, Jetpack Compose and Android application development.

## Map creation algorithm

Actual map creation is done with ForceGraph data structure.

### ForceGraph

ForceGraph data structure contain ForceNodes and ForceRelations.

ForceRelation is spring with target length and spring constant. It is always connecting exactly two ForceNodes.
If ForceRelation's actual length differs from target length, it is causing spring force between ForceNodes it connects.

ForceNode is point which have location, mass and velocity. It is connected to other ForceNodes with one or more ForceRelations.
All ForceNode's ForceRelations' spring forces are calculated together to sum force vector.
ForceNode's sum force vector will cause acceleration, which change velocity, which change location.

ForceGraph is iterating continuously by calculating:
1. Spring force (based on ForceRelation's length)
2. Acceleration
3. New velocity
4. New location
5. Changed locations are changing ForceRelation's length, so go back to step 1.

### Wifi Map

Wifi Map creation with ForceGraph...

Each Wifi scan creates one ForceNode(type=ROUTE), and each new access point from scan results creates new ForceNode(type=WIFI).
Each ForceNode(type=WIFI) is connected to ForceNode(type=ROUTE) with ForceRelation, which target length is set to estimated distance to Wifi access point.

When application is scanning more Wifi access points, ForceGraph build up so that basically every ForceNode(type=WIFI) is connected to many different ForceNode(type=ROUTE).

Now when ForceGraph is continuously iterating new locations to each ForceNode, locations of Wifi access points and routes are finding their actual locations.

## TODO list

Here is list of ideas what to do

- Functionality
  - calculation in coroutine?
  - Canvas
    - add automatic centering and scaling (pinch zoom?)
    - add wifi ap names to screen
  - BT scanning
  - more advanced methods for getting distance
    - https://developer.android.com/guide/topics/connectivity/wifi-rtt
    - https://github.com/Plinzen/android-rttmanager-sample

- Application
  - check app version number, change to use git tag version?
  - App layout (Scaffold)
  - icon
  - support for different languages (en, fi)
  - enable crashlytics
  - enable leakcanary
  - publish to play store
  - compose testing?
    - https://developer.android.com/jetpack/compose/testing
