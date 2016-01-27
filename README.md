[![Build Status](https://ci.drtshock.net/view/All/job/Supervisor/badge/icon)](https://ci.drtshock.net/view/All/job/Supervisor/)
# Supervisor

Supervisor is the ultimate lightweight plugin for debugging your Bukkit server. With its extensive API developers can easily hook into Supervisor to add 
more debuggable data to be provided when their users are prompted.

## Usage
With simplicity and lightweight being in mind, Supervisor only functions when you prompt it to, using the `svreport` command _short for 
Supervisor Report_.

The command takes a single collection of arguments. Alone, these arguments do nothing, but contexts may use these to manipulate the report. An example
of a context that uses arguments is the PluginsContext which allows you to include a plugin's config.yml by typing `config/pluginname`.

The command accepts flags ([See SupaHam's wiki for more information](https://wiki.supaham.com/Commands#Flags)) to modify the functionality of 
the report.

The following table shows the flags that are accepted by Supervisor:

| flag | value  | description |
| ---  | ---    | ---         |
| -v   | N/A    | Displays the Supervisor version. |
| -t   | String | Title of the report. This has no functionality other than maybe differentiating reports. |
| -f   | String | Format the report should be written in. (There is only JSON unless you write a custom serializer) |
| -e   | Strings | Contexts to exclude from the report, separated using `,`. e.g. `-e log`, ` -e log,anothercontext` |
| -i   | Strings | Contexts to include in the report, separated using `,`. Excludes overrides this. e.g. `-e log`, ` -e log,anothercontext` |
| -l   | Integer | Report level output. Default is 200 (BRIEF). |

The following table shows examples of the possible command usages:

| command                          | description |
| -------------------------------- | ----------- |
| /svreport                        | Generates a report with default settings. E.g. <https://gist.github.com/ff0a99061e6ca66108f4> |
| /svreport -v                     | Sends a message similar to 'Supervisor v1.1-SNAPSHOT'. |
| /svreport -t "My Amazing Report" | Generates a report with the title _My Amazing Report_. E.g. <https://gist.github.com/1178dac68be5be261f98> |
| /svreport -f JSON                | Generates a report that is outputted in JSON format. E.g. <https://gist.github.com/1178dac68be5be261f98> |
| /svreport -e log                 | Generates a report that excludes the `log` context. E.g. <https://gist.github.com/df1362d5616481c77ac7> |
| /svreport -i log                 | Generates a report that includes only the `log` context. E.g. <https://gist.github.com/ffe6d0560b658d03c349> |
| /svreport -l 0                   | Generates a report on the briefest level. E.g. <https://gist.github.com/920f43e8404ce39c5dc8> |
| /svreport -i log -e log          | Generates a report that both includes AND excludes the `log` context. E.g. <https://gist.github.com/3b7e5fd09ae202e3e67d> |
| /svreport -l 4000                | Generates a report with the output level of 4000 (CRAZY VERBOSE!). E.g. <https://gist.github.com/86280e6f172aa1d8d932> |

[Here](https://gist.github.com/anonymous/450605c76428f1a72326)'s an example of a JSON report outputted in JSON format instantly shared using the Gist service!

## API
An API is meant to be immutable, and that's what Supervisor provides. A simple and straight-to-the-point API that won't change once written.

In the following scenario, we will assume you just finished writing your awesome item-dropping plugin called **ItemDropper!** To hook into Supervisor
it's as easy as writing one Context class and registering it. That's it, you're done! Really! Here's an example

```java
public class ItemDropperContext extends ReportContext {

    public ItemDropperContext() {
        super("item-dropper-context", "Item Dropper"); // unique name, followed by a Human friendly title to represent the data output
    }

    @Override public void run(ReportContextEntry entry) {
        entry.append("drops", getDropsCount()); // Appends getDropsCount() to the entry under the key 'drops'.
    }
}
```

Then, in your main plugin class, when your plugin is being enabled, you register an instance of the class:

```java
public class ItemDropperPlugin ... {
    // ...
    @Override public void onEnable() {
    // ...
        SupervisorPlugin.get().registerContext(this, new ItemDropperContext()); // Context registered and outputted when the user creates a report!
    }
}
```

And voilà! You just hooked into Supervisor... FOREVER!

When appending to an Appendable (ReportContextEntry, ReportFile, etc.) the value may be of any type assuming the serializer can handle it.
The JSON serializer is able to handle all primitive data types, Collections, Maps, and a few others. 

## Building

To build this project simply execute the following maven command `mvn clean package`.

If you are unable to build this project, please contact SupaHam through [https://www.supaham.com/contact/](https://www.supaham.com/contact/)

## Contributing
* **This project is written in Java 7.**  Make sure to mark methods with
  ` @Override` that override methods of parent classes, or that implement
  methods of interfaces (Java 7).
* **Wrap code to a 150 column limit.**
* **Write COMPLETE Javadocs.**
* **Make sure the code is efficient.**
* **Test your code!!**
* **Use English-US dictionary** Color instead of Colour, Armor instead of Armour.

## Example
This is **GOOD:**

    if (var.func(param1, param2)) {
        // do things
    }

This is **BAD:**

    if(var.func( param1, param2 ))
    {
        // do things
    }
