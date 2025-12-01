<img width="1080" height="2280" alt="Screenshot_20251201_212333" src="https://github.com/user-attachments/assets/7ad3c215-7401-4e70-814b-c0d8c56bcb68" />MultiBank App
A simple Android application that displays 25 stock symbols with live updates. Prices refresh every 2 seconds, and the UI provides visual cues for price changes and connection status.

Features
  Live Stock Prices: Shows 25 different stock symbols with prices that update every 2 seconds.
  Dynamic Sorting: Stocks are automatically sorted from highest to lowest price after each update.
  
Price Change Indicators:
  Arrow Icon: Next to each price, an arrow indicates the price direction:
  Upwards green arrow → price increased
  Downwards red arrow → price decreased
  Price Color Flash: Price temporarily changes green/red for 1 second depending on increase or decrease.
  
WebSocket Connectivity:
  Connection Indicator: Top-left corner shows whether the app is connected to the WebSocket.
  Connect/Disconnect Switch: Top-right corner allows manually connecting or disconnecting from the WebSocket.

Tech Stack
  Language: Kotlin
  UI Framework: Jetpack Compose
  Architecture: MVI
  Dependency Injection (DI): Hilt
  Live Data: StateFlow / Shared Flow / Compose state
  Networking: okhttp3
  Material Design 3 for theming and components

Bonus tasks:
  Price flashes green for 1 second on increase, and red on decrease
  Support for light and dark themes

Installation:
  git clone https://github.com/Maksym-Kalinichenko/MultiBank.git
  Sync Gradle and build the project.
  Run the app on an emulator or a real device with minimum SDK 31.

<img width="1080" height="2280" alt="Screenshot_20251201_212333" src="https://github.com/user-attachments/assets/49dff051-f7f6-4bc6-b902-0a5fb453846a" />
<img width="1080" height="2280" alt="Screenshot_20251201_212357" src="https://github.com/user-attachments/assets/d21ff0bb-f026-4ea2-9e77-13dd6fd0ff66" />


