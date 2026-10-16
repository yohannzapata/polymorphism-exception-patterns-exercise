# Parcel Delivery System

A simple console Parcel Delivery System in Java that demonstrates polymorphism, exception handling,
and the Singleton and Strategy design patterns.

## How to run

From the project folder (Java 8 or newer):

```
javac -d out Main.java delivery/*.java exception/*.java manager/*.java model/*.java
java -cp out Main
```

## Project structure

```
Main.java        menu and input validation
model/           Parcel (abstract), Document, Box, Fragile (extends Box)
delivery/        DeliveryService (interface), StandardDelivery, ExpressDelivery, SameDayDelivery
manager/         ParcelManager (Singleton)
exception/       custom exceptions
```

## Where each concept is used

- **Polymorphism**: `Parcel` is abstract. `Document`, `Box` and `Fragile` each override
  `getType()`, `getChargeableWeight()` and `getBaseFee()`. `Fragile` extends `Box` and adds the
  Php 100 handling fee on top of the box fee.
- **Strategy pattern**: every parcel holds a `DeliveryService`. Standard, Express and Same Day each
  calculate the delivery fee their own way, and changing the service swaps the strategy.
- **Singleton pattern**: `ParcelManager` has a private constructor and one shared instance from
  `getInstance()`. It stores all parcels.
- **Exception handling**: `DuplicateTrackingNumberException`, `ParcelNotFoundException`,
  `ParcelAlreadyDeliveredException` and `SameDayNotAllowedException` are thrown by `ParcelManager`
  and `Parcel` and caught in `Main`. Invalid numbers (`NumberFormatException`) are caught and the
  user is asked again.

## Input rules

Every field is checked as it is typed; an invalid value shows a message and asks for that field again.

- **Tracking Number**: `ABC` followed by 4 digits (e.g. `ABC0001`), must not already exist.
- **Receiver Name**: 2 to 50 characters, letters only (spaces, `.` `'` `-` allowed).
- **Weight**: a number greater than 0 with at most 3 decimals, not above the type's maximum
  (Document 0.5 kg, Box 50 kg, Fragile 30 kg).
- **Pages**: whole number from 1 to 100. **Length / Width / Height**: whole number of cm above 0.
- **Handling Note**: required, up to 100 characters, must describe the handling in words.
- **Menu choices**: digits only, within the listed options.

## Example

Fragile, 40 x 20 x 10 cm, 1.8 kg, Express:
volumetric weight = 8000 / 3500 = 2.29 kg, so the chargeable weight is 3 kg.
Base fee = (Php 40 x 3) + Php 100 = Php 220. Delivery fee = Php 220 x 1.5 = **Php 330.00**.
