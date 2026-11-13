# Daily Plugin

Plugin for minecraft spigot, where player can recieve daily rewards for login. For version (1.21.4-26.3)

## Config

Main yml file of config

```yaml
#Language, to set give a name of file in languages directory, built in: en,pl
language: en

#how long is a day in milliseconds, build in value is 24 hours
how-long-is-day: 86400000

#config to gui
gui:
  title: "§aDaily"
  size: 54

#config to command
#to change you must restart server
command:
  name: daily
  description: Everything about daily
  usage: "/daily [add,lore,name,reload,set,remove]"
  aliases: []
  default-permission: "daily.command.default"
  default-permission-state: true
  admin-permission: "daily.command.admin"
```

## Usage

Main command which is used to configuration and opening main gui

```bash
/daily
```

### Add subcommand

Every possibility with add subcommand

#### Add item command

Adds item as rewards to day(You must have item in hand)

```bash
/daily add item <day>
```

#### Add "command" command

Adds command to be executed when reward is received

```bash
/daily add command <day> <command>
```

#### Add type command

Adds type as an item which can be later used on days to be displayed that wasn't yet received

```bash
/daily add type <type_name>
```

#### Add opened command

Adds opened item which is displayed when daily was already received

```bash
/daily add opened
```

#### Add can open command

Adds item which is displayed when player can receive today's reward

```bash
/daily add can_open
```

#### Add decoration command

Adds item decoration to gui

```bash
/daily add decoration <slot>
```

### Set subcommand

Every possibility with set subcommand

#### Set type command

Used to sets types on days to be displayed that wasn't yet received

```bash
/daily set <day> type <type_name>
```

#### Set slot command

Used to sets slot on days so item knows where to be displayed(if doesn't specified then slot is number of day minus 1)

```bash
/daily set <day> type <type_name>
```

### Lore subcommand

Every possibility with lore subcommand

#### Lore add command

Used to add lines to lore to be displayed for specified day

```bash
/daily lore add <day> <lore>
```

#### Lore set command

Used to set line of lore to be displayed for specified day

```bash
/daily lore set <day> <index> <lore>
```

### Name subcommand

Sets name of item in gui for specified day

```bash
/daily name <day> <name>
```


## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.