package dev.xclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class XClientConfig {
    final Properties values=new Properties();
    private final Path file=FabricLoader.getInstance().getConfigDir().resolve("xclient.properties");
    public int fpsX=8,fpsY=8,coordsX=8,coordsY=22,armorX=8,armorY=42,potionX=8,potionY=70,
        durabilityX=8,durabilityY=150,keysX=8,keysY=180,infoX=8,infoY=250,hudColor=0xFFFFFF;
    public String potionPreset="Default";
    public void load(){
        if(Files.exists(file))try(InputStream in=Files.newInputStream(file)){values.load(in);}catch(IOException ignored){}
        fpsX=i("hud.fps.x",8);fpsY=i("hud.fps.y",8);coordsX=i("hud.coords.x",8);coordsY=i("hud.coords.y",22);
        armorX=i("hud.armor.x",8);armorY=i("hud.armor.y",42);potionX=i("hud.potion.x",8);potionY=i("hud.potion.y",70);
        durabilityX=i("hud.durability.x",8);durabilityY=i("hud.durability.y",150);keysX=i("hud.keys.x",8);keysY=i("hud.keys.y",180);
        infoX=i("hud.info.x",8);infoY=i("hud.info.y",250);hudColor=i("hud.color",0xFFFFFF);
        potionPreset=values.getProperty("potion.preset","Default");
    }
    private int i(String k,int d){try{return Integer.parseInt(values.getProperty(k,Integer.toString(d)));}catch(NumberFormatException e){return d;}}
    public void save(){
        put("hud.fps.x",fpsX);put("hud.fps.y",fpsY);put("hud.coords.x",coordsX);put("hud.coords.y",coordsY);
        put("hud.armor.x",armorX);put("hud.armor.y",armorY);put("hud.potion.x",potionX);put("hud.potion.y",potionY);
        put("hud.durability.x",durabilityX);put("hud.durability.y",durabilityY);put("hud.keys.x",keysX);put("hud.keys.y",keysY);
        put("hud.info.x",infoX);put("hud.info.y",infoY);put("hud.color",hudColor);values.setProperty("potion.preset",potionPreset);
        try{Files.createDirectories(file.getParent());try(OutputStream out=Files.newOutputStream(file)){values.store(out,"XClient configuration");}}catch(IOException ignored){}
    }
    private void put(String k,int v){values.setProperty(k,Integer.toString(v));}
}
