package com.soulbind.dataattachements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SoulmateData(String string) {
    //followed same example here: https://gist.github.com/Linguardium/cebcd41c6bbcd74eaa1f8b40ec2bbec8#file-modattachmenttypes-java



    // Codecs are used to serialize and deserialize data to different formats.
    // We build a codec here to tell it how to turn our ModCustomAttachedData object into Nbt, json, etc
    // https://docs.fabricmc.net/develop/codecs
    public static Codec<SoulmateData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("string").forGetter(SoulmateData::string) // our object just has a list of floats
    ).apply(instance, SoulmateData::new)); // all the values we defined above are passed to the factory method we reference here
    // SomeClass::new is a reference to a constructor. so in this case the list of floats is passed to the constructor

    // A packet codec is used to convert the object into a byte buffer for sending over the network or converting back into an object
    // There are better ways to do this, such as using PacketCodec.tuple to build the read/write methods,
    // but for simplicity of the example, this will do.
    // https://wiki.fabricmc.net/tutorial:networking#networking_in_1205
    public static PacketCodec<ByteBuf, SoulmateData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    // A default value we can use as an "empty" or reset data component
    // it uses List.of() which creates an empty, immutable list.
    public static SoulmateData DEFAULT = new SoulmateData("");

    // helper method for adding values and returning a new attached data object
    // This ensures that when we add things, we are provided the new component to set
    // for persistence and syncing, it uses the new object to determine the need to save
    // modifying the existing object will not trigger a requirement to save or sync the data
    public SoulmateData writeString(String value) {
        String string1;
        string1 = value;

        return new SoulmateData(string1); // makes the list immutable to prevent accidental modification
    }

    // helper method for removing values and returning a new attached data object
    // same as above
    public SoulmateData removeSoulmate() {



        return new SoulmateData(""); // makes the list immutable to prevent accidental modification
    }

    public SoulmateData clear() { // clear method, just returns the default empty component
        return DEFAULT;
    }
}
