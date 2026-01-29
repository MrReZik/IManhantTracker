# 🧭 IManhuntTracker

[![Version](https://img.shields.io/badge/Version-1.0-blue.svg)](https://github.com/MrReZik/AdvancedManhuntTracker/releases)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.8--1.21-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.java.com/)
[![Author](https://img.shields.io/badge/Author-MrReZik-red.svg)](https://github.com/MrReZik)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

**IManhuntTracker** is a lightweight and optimized plugin for the "Manhunt" game mode. It adds a functional compass that tracks a target player in real-time, with support for all dimensions (Nether/End) and Minecraft versions ranging from 1.8 to 1.21.

### ✨ Features

* **⚡ Multi-version Support:** Works on all server cores (Spigot/Paper) from **1.8.8** to **1.21+**.
* **🌍 Dimension Logic:**
    * If the Hunter and Speedrunner are in the **same world**: The compass points directly to the target.
    * If they are in **different worlds**: The compass points to the world spawn, simulating signal loss, and notifies the hunter (includes anti-spam protection).
* **🛠 Fully Configurable:** All messages, item names, and lore are customizable in `config.yml`.
* **🚀 Optimized:** Target tracking is handled asynchronously or with minimal impact on the main thread.

### 📜 Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/tracker <player>` | Set a target for the compass and receive the item | `manhunt.use` |
| `/tracker` | Reset the target and disable tracking | `manhunt.use` |

### ⚙️ Configuration (config.yml)

You can customize the item appearance and chat messages:

```yaml
compass-name: "&c&lHunter Compass"
compass-lore:
  - "&7Pointing to:"
  - "&e%player%"
  - "&7Right-click to refresh."

messages:
  target-set: "&aTarget set: &e%player%"
  target-reset: "&cTarget reset."
  target-offline: "&cPlayer not found or offline."
  target-diff-world: "&c&lWARNING: &eTarget is in another dimension!"
  only-players: "&cOnly players can use this command."
