package com.soulbind.items;

import com.soulbind.SoulBind;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {
    // its good practice to split stuff up in different folders and classes. IE, one for blocks, one for items, whatever.





    // I just took this from the fabric wiki https://docs.fabricmc.net/develop/items/first-item



    // register the soul token, once again, just copied from fabric wiki.
    // the Item.Settings() can be changed to easily do stuff like max stack size, which is good in this case cause you only get one token anyway.
    // rarity.epic changes the item color name. you can middleclick the rarity.epic to find out what other rarities there are.
    // you can put any other setting after one another. there is not limit.
    // SoulToken::new can be Item::new for simple items, but in this case we want to do more stuff with it like do X every game tick, so I made a soultoken item class which extends Item.class,
    // you can do that with anything that needs a specific class, I.E if something needs a Block::new you can just make a custom class and have it extend Block and it will work.
    public static final Item SOUL_TOKEN = register("soul_token", SoulToken::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC));




    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SoulBind.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }


    // this class gets called in the main Soulbind initalizer class to basically make this class known, honestly dont know why really but its important in each one of these ModX classes like ModBlocks.
    public static void initialize() {
    }

}
