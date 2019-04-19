# Towny3D
## The plugin is still in alpha, please report any bug/suggestion [here](https://sndev.it/c/support)

### Dependencies:
- Vault
### Description:
With Towny3D you can finally create 3D claims and have multiple plots in the same chunk but at different heights! You just have to select the lower point and the higher point of the plot and then run the claim command.
The plugin has also all the features of Towny! (not all already implemented since the plugin is in alpha)

[![Donate!](https://proxy.spigotmc.org/c3159ca56058cc7d0de785cb0d2ba3473ac6a50d?url=https%3A%2F%2Fwww.umassmed.edu%2Fglobalassets%2Fmueller-lab-for-gene-therapy%2Fsupport-us-button.png)](https://www.paypal.me/tigierrei)

### Commands:
  - /town
    - help (or) ?
    - create <townName>
    - mayor
    - help
    - getClaimerStick (or) gcs Gives the item to set the lower and higher points of the plot
    - addAssistant <playerName>
    - removeAssistant <playerName>
    - claim [chunk]
    - unclaim
    - set
      - perm <permName>
    - toggle <flag>
    - invite (or) add <playerName>
    - deposit <amount>
    - withdraw <amount>
    - setHome
    - online Shows the list of the online residents of your town
    - here Shows the info of the town you are standing in
    - spawn
    - kick <playerName>
    - disband
    - <townName> Shows the info of the specified town
  - /plot
    - help
    - set
      - embassy
      - perm <permName>
      - reset
    - toggle <flag>
    - forsale (or) fs <price>
    - notforsale (or) nfs
    - claim
    - unclaim
    - name Sets the name of the plot
    - info Shows the info of the plot
    - showBorders (or sb) shows the plot borders using diamond blocks
    - hideBorders (or sb) hides the plot borders shown by /plot sb command
  - /townchat (or) tc
  - /townyadmin
    - chat
    - spy
  - /accept
  - /decline
  
### Permissions:
```
towny3d.*:
    default: op
    description: Implies all plugin permisisons
    children:
      town.*: true
      plot.*: true
      towny3d.admin.*: true
  town.*:
    default: op
    description: Implies all town permissions
    children:
      town.help: true
      town.create: true
      town.mayor: true
      town.claim: true
      town.invite: true
      town.deposit: true
      town.withdraw: true
      town.info: true
      town.kick: true
      town.spawn: true
      town.here: true
      town.online: true
      town.disband: true
  town.help:
    default: true
  town.create:
    default: op
  town.mayor:
    default: true
  town.claim:
    default: true
  town.invite:
    default: true
  town.deposit:
    default: true
  town.withdraw:
    default: true
  town.info:
    default: true
  town.kick:
    default: true
  town.spawn:
    default: true
  town.here:
    default: true
  town.online:
    default: true
  town.disband:
    default: op
  plot.*:
    default: op
    description: Implies all plot permissions
    children:
      plot.toggle.*: true
      plot.perm.*: true
      plot.sell: true
      plot.claim: true
      plot.embassy: true
      plot.name: true
      plot.info: true
  plot.info:
    default: true
  plot.name:
    default: true
  plot.sell:
    default: true
  plot.claim:
    default: true
  plot.embassy:
    default: true
  plot.perm.*:
    default: true
    children:
      plot.perm.pvp: true
      plot.perm.interact: true
      plot.perm.inventory: true
      plot.perm.build: true
      plot.perm.destroy: true
      plot.perm.drop: true
      plot.perm.pick: true
      plot.perm.move: true
  plot.toggle.*:
    default: op
    description: Implies all plot.setflag permissions
    children:
      plot.toggle.fire: true
      plot.toggle.explosion: true
      plot.toggle.friendly_mobs_spawn: true
      plot.toggle.hostile_mobs_spawn: true
  plot.toggle.fire:
    default: false
  plot.toggle.explosion:
    default: false
  plot.toggle.friendly_mobs_spawn:
    default: false
  plot.toggle.hostile_mobs_spawn:
    default: false
  plot.perm.pvp:
    default: false
  plot.perm.interact:
    default: false
  plot.perm.inventory:
    default: false
  plot.perm.build:
    default: false
  plot.perm.destroy:
    default: false
  plot.perm.drop:
    default: false
  plot.perm.pick:
    default: false
  plot.perm.move:
    default: false
  towny3d.admin.*:
    description: Implies all admin permissions
    default: op
    children:
      town3d.admin.chat.*: true
  towny3d.admin.chat.*:
    description: Implies all chat permissions
    default: op
    children:
      towny3d.admin.chat.spy: true
  towny3d.admin.chat.spy:
    default: op
```
