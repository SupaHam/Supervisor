# Supervisor

Supervisor is the ultimate lightweight plugin for debugging your Bukkit server. With its extensive API developers can easily hook into Supervisor to add 
more debuggable data to be provided when their users are prompted.

## Usage
With simplicity and lightweight being in mind, Supervisor only functions when you prompt it to, using the `svreport` command _short for 
Supervisor Report_.

[Here](https://gist.github.com/anonymous/ce685479dcfb281c06ac)'s an example of a JSON report outputted in JSON format instantly shared using the Gist service!

## API
An API is meant to be immutable, and that's what Supervisor provides. A simple and straight-to-the-point API that won't change once written.

In the following scenario, we will assume you just finished writing your awesome item-dropping plugin called **ItemDropper!** To hook into Supervisor
it's as easy as writing one Context class and registering it. That's it, you're done! Really! Here's an example

```java
public class ItemDropperContext extends SimpleContext {

    public ItemDropperContext() {
        super("item-dropper-context", "Item Dropper"); // unique name, followed by a Human friendly title to represent the data output
    }

    @Override public void run() {
        append("drops", dropsCount);
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
