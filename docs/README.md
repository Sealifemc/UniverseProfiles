---
description: A Minecraft plugin for displaying player profiles with customizable GUI including equipment, cosmetics, a star system, and interactive action buttons.
cover: .gitbook/assets/cover.png
coverY: 0
layout:
  cover:
    visible: true
    size: hero
  title:
    visible: true
  description:
    visible: true
  tableOfContents:
    visible: true
  outline:
    visible: true
  pagination:
    visible: true
---

# 🌟 UniverseProfiles

## Overview

**UniverseProfiles** is a powerful and customizable Minecraft plugin that allows players to view detailed profile information through an interactive GUI. Display equipment, cosmetics, statistics, and enable social interactions with an elegant and modern interface.

{% hint style="info" %}
**Version:** 1.0
**Minecraft:** 1.16.5 - 1.21+
**Platform:** Paper/Folia
{% endhint %}

## ✨ Key Features

### 🎨 Customizable GUI
- Fully configurable interface with MiniMessage formatting
- Custom model data support (1.20.5+) and item model support (1.21.2+)
- Player head displays with custom textures
- Equipment slots (armor + main/off hand)
- Cosmetic integration (HMCCosmetics & ECosmetics)
- PlaceholderAPI support in titles, names, and lore

### ⭐ Star System
- Players can give/remove stars to other profiles
- Configurable self-starring option
- Track popularity and reputation

### 🔗 Integrations
- **PlaceholderAPI** - Display any placeholder in your GUI
- **HMCCosmetics** - Show player cosmetics (helmet, backpack, balloon, offhand)
- **Vault** (optional) - Economy and permission integration

### 💾 Multi-Database Support
- **SQLite** - Local file database (recommended for small servers)
- **MySQL** - External database (recommended for networks)
- **Redis** - Cache layer for improved performance

### 🚀 Performance
- Folia compatible with automatic scheduler detection
- Async database operations
- Configurable caching system
- Batch operations for optimal performance

## 📸 Screenshots

{% hint style="warning" %}
**Add screenshots here**
Place your images in `docs/.gitbook/assets/` folder
{% endhint %}

<figure><img src=".gitbook/assets/profile-gui-example.png" alt="Profile GUI Example"><figcaption><p>Example of a player profile with cosmetics and equipment</p></figcaption></figure>

## 🎯 Use Cases

- **Social servers** - Let players interact and give recognition
- **RPG servers** - Display player stats and equipment
- **Network hubs** - Centralized player information
- **Community building** - Foster player interactions

## 🔥 Quick Start

{% content-ref url="getting-started/installation.md" %}
[installation.md](getting-started/installation.md)
{% endcontent-ref %}

{% content-ref url="getting-started/quick-setup.md" %}
[quick-setup.md](getting-started/quick-setup.md)
{% endcontent-ref %}

## 📚 Documentation

<table data-view="cards">
  <thead>
    <tr>
      <th></th>
      <th></th>
      <th data-hidden data-card-cover data-type="files"></th>
      <th data-hidden data-card-target data-type="content-ref"></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Configuration</strong></td>
      <td>Learn how to configure the plugin</td>
      <td></td>
      <td><a href="configuration/config.yml.md">config.yml.md</a></td>
    </tr>
    <tr>
      <td><strong>GUI Setup</strong></td>
      <td>Customize your profile interface</td>
      <td></td>
      <td><a href="configuration/gui.yml.md">gui.yml.md</a></td>
    </tr>
    <tr>
      <td><strong>Commands</strong></td>
      <td>All available commands and permissions</td>
      <td></td>
      <td><a href="usage/commands.md">commands.md</a></td>
    </tr>
  </tbody>
</table>