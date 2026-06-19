# Osmos Flutter Ad Integration Demo

## Overview

This project demonstrates the integration of the Osmos Android SDK with a Flutter application using Platform Channels.

The application:

* Initializes the Osmos SDK
* Fetches display ads from the Osmos Ad Server
* Displays ad creatives inside Flutter UI
* Tracks ad impressions
* Tracks ad clicks
* Handles errors gracefully
* Provides fallback UI when ads are unavailable


# Project Architecture

Flutter UI
    │
    ▼
Ads Bloc / Service Layer
    │
    ▼
MethodChannel ("osmos")
    │
    ▼
Android Native (Kotlin)
    │
    ▼
Osmos SDK
    │
    ├── Fetch Ads
    ├── Track Impression
    └── Track Click

### Components

#### Flutter Layer

Responsible for:

* Ad rendering
* User interaction
* Visibility detection
* Triggering native methods

#### Native Android Layer

Responsible for:

* SDK initialization
* Ad fetching
* Impression tracking
* Click tracking

#### Osmos SDK

Responsible for:

* Ad serving
* Event tracking
* Analytics collection

---

# Setup Steps

## Prerequisites

* Flutter SDK
* Android Studio
* Android Device or Emulator
* Java 17+

## Clone Repository

git clone [repository-url](https://github.com/Sujith-33/osmos-demo)
cd osmos

## Install Dependencies

flutter pub get

## Run Application

flutter run

# SDK Initialization

The Osmos SDK is initialized inside the Android application layer before any ad request is made.

Initialization includes:

* Client ID configuration
* SDK instance creation
* Global instance registration

The SDK must be initialized successfully before calling:

* fetchDisplayAdsWithAu()
* registerAdImpressionEvent()
* registerAdClickEvent()


# Ad Fetching Flow

The application fetches ads through the Osmos SDK.

### Flow

1. Flutter requests an ad.
2. MethodChannel calls native Kotlin code.
3. Kotlin invokes:

fetchDisplayAdsWithAu(...)

4. SDK returns ad response.
5. Ad information is parsed:

* Image URL
* Click URL
* UCLID

6. Data is sent back to Flutter.
7. Flutter renders the ad.

### Response Data

The following fields are extracted:

{
  "imageUrl": "...",
  "destinationUrl": "...",
  "uclid": "..."
}


# Impression Tracking

## Requirement

An impression should only be fired when at least 50% of the ad is visible to the user.

## Implementation

Flutter uses a visibility detection mechanism to monitor the ad widget.

When:

Visible Area >= 50%

the impression event is triggered only once.

### Flow

Ad Visible
    │
    ▼
Visibility >= 50%
    │
    ▼
trackImpression()
    │
    ▼
MethodChannel
    │
    ▼
registerAdImpressionEvent()

### Native Call

registerAdImpressionEvent(
    cliUbid,
    uclid,
    position,
    trackingParams,
    errorCallback
)

### Duplicate Prevention

A local flag is maintained so that the same impression is not fired multiple times.


# Click Tracking

When the user taps the advertisement:

1. Click event is triggered.
2. Flutter calls native code.
3. Native layer invokes:

registerAdClickEvent(...)

4. SDK records click analytics.
5. User is redirected to destination URL.

### Flow

User Click
    │
    ▼
trackClick()
    │
    ▼
MethodChannel
    │
    ▼
registerAdClickEvent()

# Event Logging

The following events are logged using Logcat.

### Ad Loaded

OSMOS_RESPONSE

### Ad Failed

OSMOS_ERROR


### Impression Fired

OSMOS_IMPRESSION

### Click Fired

OSMOS_CLICK


# Error Handling & Resilience

The application handles the following scenarios:

## SDK Initialization Failure

* Error captured
* Application does not crash

## Network Failure

* Error logged
* Fallback UI shown

## No Ads Returned

If SDK returns:

null

or empty response:

Ad not available

is displayed.

## Invalid Response Data

Missing fields are handled safely using null checks and default values.


# Fallback UI

When ads cannot be loaded:

“Ad not available”

is displayed to the user.

This prevents application crashes and provides a graceful user experience.

# UX Optimizations

## Loading State

A loading indicator is shown while the ad request is in progress.

## Duplicate Requests Prevention

Ad requests are controlled to avoid unnecessary duplicate network calls.

## Impression Deduplication

Impressions are fired only once per ad.

## Lifecycle Handling

The implementation works correctly across:

* App resume
* App pause
* Activity recreation

## Screen Rotation

Flutter state management ensures ads are not repeatedly tracked because of screen rotation.

# Assumptions Made

1. A valid Osmos Client ID is provided.
2. SDK initialization occurs before ad requests.
3. Internet connectivity is available.
4. UCLID is always present in a valid ad response.
5. Impression should only be counted once per ad instance.

# Challenges Faced

## SDK Reverse Engineering

The SDK was distributed as a JAR, requiring inspection using:

javap

to discover available APIs and parameters.

## Native-Flutter Communication

MethodChannel integration was required to bridge Flutter and Android SDK functionality.

## Event Tracking

Understanding the required parameters for:

* Impression tracking
* Click tracking

required exploration of SDK interfaces and builders.

## Network Variability

Different networks occasionally returned null ad responses, requiring additional error handling and fallback UI support.

# How To Run The Demo

## Step 1

Connect an Android device or start an emulator.

## Step 2

Run:

flutter pub get

## Step 3

Run:

flutter run


## Step 4

Tap "Load Ad".

## Step 5

Verify:

* Ad loads successfully
* Impression event fires when 50% visible
* Click event fires on tap
* Logs appear in Logcat

### Expected Log Tags

OSMOS_RESPONSE
OSMOS_IMPRESSION
OSMOS_CLICK
OSMOS_ERROR


# Conclusion

This project demonstrates a complete Flutter-to-Native integration of the Osmos SDK, including ad fetching, impression tracking, click tracking, error handling, lifecycle support, and fallback mechanisms while maintaining a stable user experience.
