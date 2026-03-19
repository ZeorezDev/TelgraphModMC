
---

# Telegraph Voice Relay (Minecraft 1.20.1)

## Overview

**Telegraph Voice Relay** is a lightweight Minecraft Forge mod for **1.20.1** that allows players to transmit communication between locations using a cable-connected telegraph system.

The mod is designed as an extension for voice-based gameplay, enabling long-distance communication through **connected telegraph machines**.

---

## ⚠️ Required Dependency

This mod **requires** Simple Voice Chat to function.

* The mod depends on its API
* It must be installed on both **client and server**
* Without it, the mod will **not work**

---

## Features

### 📡 Telegraph Network System

* Place **Telegraph Machines** and connect them using cables
* Machines form a **network** through connected blocks
* Messages are transmitted across all connected machines

---

### 💬 Command-Based Messaging

* Use the command:

```
/telegraph <message>
```

* Sends a message to all connected telegraph machines
* Includes:

  * Player name in message
  * Cooldown system (prevents spam)

---

### 🔊 Audio & Visual Feedback

* Sending machine:

  * Emits **red particles**
* Receiving machines:

  * Emit **green particles**
  * Play telegraph beep sound

---

### 🔗 Network Detection

* Automatically detects:

  * Nearby telegraph machine (within range)
  * Connected network using cable blocks
* Sends message to all valid endpoints

---

## 📦 Item Access (Important)

* Items are **NOT available in the Creative Tab**
* You must use commands to obtain them:

```
/give @p telegraph:<item_name>
```

---

## Design Goals

* Lightweight and simple implementation
* Focus on **network-based communication**
* Extend voice/chat systems instead of replacing them
* Suitable for multiplayer and RP scenarios

---

## Compatibility

* Minecraft Forge **1.20.1**
* Requires:

  * Simple Voice Chat

---

## License

Licensed under **GPL-3.0 license**.

---

## Notes

This mod is not intended to be a full communication overhaul.

It provides a **basic, extendable telegraph-style relay system** that can be used in:

* Roleplay servers
* Nations / war-themed modpacks
* Multiplayer communication setups

---
