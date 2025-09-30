---
description: Language files configuration and customization
---

# 🌍 Languages

## Overview

UniverseProfiles utilise des fichiers de langue au format YAML avec support MiniMessage pour les couleurs et formatage.

## Langues disponibles

| Langue | Code | Fichier |
|--------|------|---------|
| 🇺🇸 English | `en_US` | `languages/en_US.yml` |
| 🇫🇷 Français | `fr_FR` | `languages/fr_FR.yml` |

## Fichier en_US.yml

{% code title="languages/en_US.yml" lineNumbers="true" %}
```yaml
prefix: "<gradient:#FF0080:#8000FF>[UniverseProfiles]</gradient> "

messages:
  no_permission: "<red>You don't have permission to use this command!</red>"
  player_not_found: "<red>Player %player% not found!</red>"
  profile_private: "<red>This player's profile is private!</red>"
  profile_now_public: "<green>Your profile is now public!</green>"
  profile_now_private: "<green>Your profile is now private!</green>"
  star_given: "<green>You gave a star to %player%!</green>"
  star_received: "<gold>%player% gave you a star! ⭐</gold>"
  already_starred: "<red>You already gave a star to this player!</red>"
  cannot_star_self: "<red>You cannot give yourself a star!</red>"
  reload_success: "<green>Plugin reloaded successfully!</green>"

commands:
  usage:
    main: "<gray>Usage: /profile [player]</gray>"
    admin: "<gray>Usage: /profile admin <subcommand> [args]</gray>"

privacy:
  public: "<green>Public</green>"
  private: "<red>Private</red>"

gui:
  loading: "<yellow>Loading profile...</yellow>"
  error: "<red>Error loading profile!</red>"
```
{% endcode %}

## Fichier fr_FR.yml

{% code title="languages/fr_FR.yml" lineNumbers="true" %}
```yaml
prefix: "<gradient:#FF0080:#8000FF>[UniverseProfiles]</gradient> "

messages:
  no_permission: "<red>Vous n'avez pas la permission d'utiliser cette commande !</red>"
  player_not_found: "<red>Joueur %player% introuvable !</red>"
  profile_private: "<red>Le profil de ce joueur est privé !</red>"
  profile_now_public: "<green>Votre profil est maintenant public !</green>"
  profile_now_private: "<green>Votre profil est maintenant privé !</green>"
  star_given: "<green>Vous avez donné une étoile à %player% !</green>"
  star_received: "<gold>%player% vous a donné une étoile ! ⭐</gold>"
  already_starred: "<red>Vous avez déjà donné une étoile à ce joueur !</red>"
  cannot_star_self: "<red>Vous ne pouvez pas vous donner une étoile !</red>"
  reload_success: "<green>Plugin rechargé avec succès !</green>"

commands:
  usage:
    main: "<gray>Utilisation : /profile [joueur]</gray>"
    admin: "<gray>Utilisation : /profile admin <sous-commande> [args]</gray>"

privacy:
  public: "<green>Public</green>"
  private: "<red>Privé</red>"

gui:
  loading: "<yellow>Chargement du profil...</yellow>"
  error: "<red>Erreur lors du chargement du profil !</red>"
```
{% endcode %}

## Placeholders disponibles

| Placeholder | Description | Exemple |
|-------------|-------------|---------|
| `%player%` | Nom du joueur concerné | `Steve` |
| `%viewer%` | Nom du joueur qui regarde | `Alex` |

## Personnalisation

### Créer une nouvelle langue

1. Copier `en_US.yml` vers `languages/ma_langue.yml`
2. Traduire tous les messages
3. Modifier `config.yml` :

{% code title="config.yml" %}
```yaml
language: ma_langue
```
{% endcode %}

### Personnaliser les couleurs

UniverseProfiles utilise **MiniMessage** pour le formatage.

{% tabs %}
{% tab title="Couleurs simples" %}
{% code title="Couleurs de base" %}
```yaml
message: "<red>Texte rouge</red>"
message: "<green>Texte vert</green>"
message: "<gold>Texte doré</gold>"
message: "<blue>Texte bleu</blue>"
```
{% endcode %}
{% endtab %}

{% tab title="Gradients" %}
{% code title="Dégradés de couleurs" %}
```yaml
prefix: "<gradient:#FF0080:#8000FF>[UniverseProfiles]</gradient>"
message: "<gradient:red:blue:green>Multi-color gradient</gradient>"
```
{% endcode %}
{% endtab %}

{% tab title="Formatage" %}
{% code title="Bold, italic, underline" %}
```yaml
message: "<bold>Texte gras</bold>"
message: "<italic>Texte italique</italic>"
message: "<underlined>Texte souligné</underlined>"
message: "<bold><red>Gras et rouge</red></bold>"
```
{% endcode %}
{% endtab %}
{% endtabs %}

### Exemples de personnalisation

#### Style minimaliste

{% code title="languages/minimal_en.yml" %}
```yaml
prefix: "[UP] "

messages:
  no_permission: "No permission"
  player_not_found: "Player not found: %player%"
  star_given: "Star given to %player%"
  star_received: "%player% gave you a star"
```
{% endcode %}

#### Style coloré RGB

{% code title="languages/colorful_en.yml" %}
```yaml
prefix: "<gradient:#00ff00:#00ffff:#0000ff>[UniverseProfiles]</gradient> "

messages:
  star_given: "<gradient:#ffd700:#ffA500>⭐ Star given to %player%!</gradient>"
  star_received: "<rainbow>%player% gave you a star! ⭐</rainbow>"
```
{% endcode %}

## Documentation MiniMessage

Pour plus d'options de formatage, consulter :
👉 [MiniMessage Documentation](https://docs.advntr.dev/minimessage/format.html)

### Tags disponibles

| Tag | Description | Exemple |
|-----|-------------|---------|
| `<red>` | Couleur rouge | `<red>text</red>` |
| `<#RRGGBB>` | Couleur HEX | `<#FF5733>text</#FF5733>` |
| `<gradient>` | Gradient | `<gradient:red:blue>text</gradient>` |
| `<rainbow>` | Arc-en-ciel | `<rainbow>text</rainbow>` |
| `<bold>` | Gras | `<bold>text</bold>` |
| `<italic>` | Italique | `<italic>text</italic>` |
| `<underlined>` | Souligné | `<underlined>text</underlined>` |
| `<strikethrough>` | Barré | `<strikethrough>text</strikethrough>` |

## Recharger les langues

Après modification :

```
/profile reload
```

Aucun redémarrage nécessaire !

## Voir aussi

{% content-ref url="config.yml.md" %}
[config.yml.md](config.yml.md)
{% endcontent-ref %}
