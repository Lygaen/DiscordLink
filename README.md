[![Java CI](https://github.com/Lygaen/DiscordLink/actions/workflows/main.yml/badge.svg)](https://github.com/Lygaen/DiscordLink/actions/workflows/main.yml)
# DISCORD LINK
Make a discord link between a Minecraft user and his discord account.

## Summary
Here is a summary of the content :
1. [Jar](https://github.com/Lygaen/DiscordLink#jar)
2. [Config](https://github.com/Lygaen/DiscordLink#config)
3. [Commands](https://github.com/Lygaen/DiscordLink#commands)

## Jar
First, you'll need to download the latest jar from [the releases tab](https://github.com/Lygaen/DiscordLink/releases).
It will be the core of the plugin. Then, you'll place it under the folder (in your server) : `plugins`.
So you should have something like :
<details>
    <summary>Example plugin folder</summary>
    <pre>
    │
    ├───plugins
    │   │   DiscordLink.jar
    │   │
    │   ├───bStats
    │   │       config.yml
    │   │
    │   └───DiscordLink
    │           config.yml
    </pre>
</details>

## Config
Then, you can configure the plugin's config file 
(under `plugins/DiscordLink/config.yml`) using the marking language that
is using the spigot server : [YAML](https://fr.wikipedia.org/wiki/YAML).

### Table of content
Each of the topics will be given an example as well.
1. [Token](https://github.com/Lygaen/DiscordLink#token)
2. [Guild ID](https://github.com/Lygaen/DiscordLink#guild-id)
3. [Presence](https://github.com/Lygaen/DiscordLink#presence)
4. [Channels](https://github.com/Lygaen/DiscordLink#channels)

#### Token
You can get your token by creating a discord bot on [the dev portal](https://discord.com/developers/applications),
then get the token of the bot that you created (you can follow tutorials on the web).
<br>
Then replace the config value (by default `yourToken`) with the said token.
```yaml
token: ODMwODMyOTI5NTUwNzYxOTg1.YHMbdg.BSUJ4gJ-f3XAdPC9QisUWJYYqeU
# Example token that doesn't work
```

#### Guild Id
In order for the bot to work, you'll need to fill in the `guild-id` field with your
guild id on where the bot will be operating (Yet to support multi-servers).
```yaml
guild-id: 886593349765709905
```

#### Presence
The plugin now supports presence ! You can give your bot a presence (equivalent to activity).
- The `status` field must be any of `online`, `dnd`, `idle` or `offline`.
- The `type` one must be either `listening`, `watching`, `playing`, `competing` or `streaming`, but
if streaming is set, a url must be given as well.
- The `text` field represents what will be shown next to the activity type 
(eg : "**Watching** the Minecraft Server !")
- And finally the `url` field **must** be a valid url : which is a derived from 
`https://twitch.tv/` or `https://youtube.com/watch?v=`. This field will be ignored 
if the `type` is not set to `streaming` but will throw an error if absent or empty.
```yaml
presence:
  status: online
  type: watching
  text: the Minecraft Server !
  url: none
```

#### Channels
Here is all of the power of the Discord Link : its custom channel accessible in game.
You can add as much entries as you want to the `channels` section with the exception
of it following these rules :
- All fields must be set (display and id),
- `display` contains the name that will be displayed in-game for the name of a channel,
- `id` is a channel id and must be valid,
- The name of each sections will determine what will be the id selector : if you name
your section `vocal-1`, the selector id of that channel would be `vocal-1`.
```yaml
channels:
  vocal-1:
    display: "§eVOCAL 1"
    id: 852605875571261520
  vocal-2:
    display: "§eVOCAL 2"
    id: 852605895246086154
  vocal-3:
    display: "§eVOCAL 3"
    id: 852605914032242708
```
## Commands
You can simply give all permissions with the permission (`discordlink.all`),
here is the current list of the commands :
| Name     	| Permissions               	| Usage            	| Explanation                 	|
|----------	|---------------------------	|------------------	|-----------------------------	|
| Move     	| discordlink.move          	| /move [channel]   | Moves you in a channel       	|
| Move     	| discordlink.move.others       | /move [player] [channel]   | Moves someone in a channel       	|
| Mute     	| discordlink.mute          	| /mute            	| Mutes you in discord        	|
|          	| discordlink.mute.others   	| /mute [player]   	| Mutes someone else          	|
| Deafen   	| discordlink.deafen        	| /deafen          	| Deafens you                 	|
|          	| discordlink.deafen.others 	| /deafen [player] 	| Deafens someone else        	|
| Channels 	| discordlink.channels      	| /channels        	| List all available channels 	|
| Link     	| discordlink.link          	| /link            	| Starts the linking process  	|
| Unlink   	| discordlink.unlink        	| /unlink          	| Unlinks you                 	|
