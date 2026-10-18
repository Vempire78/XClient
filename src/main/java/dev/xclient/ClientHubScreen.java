package dev.xclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class ClientHubScreen extends Screen {
    private final ModuleManager modules;private final XClientConfig config;private final Screen parent;
    public ClientHubScreen(ModuleManager m,XClientConfig c,Screen p){super(Text.literal("XClient Settings"));modules=m;config=c;parent=p;}
    @Override protected void init(){
        int x=width/2-105,y=height/2-75;
        addDrawableChild(ButtonWidget.builder(Text.literal("Edit HUD positions"),b->client.setScreen(new HudEditorScreen(config,this))).dimensions(x,y,210,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Potion preset: "+config.potionPreset),b->{String[] p={"Default","JoZo","Lunar","Feather"};int i=0;for(int j=0;j<p.length;j++)if(p[j].equals(config.potionPreset))i=(j+1)%p.length;config.potionPreset=p[i];config.save();clearAndInit();}).dimensions(x,y+25,210,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cycle HUD color"),b->{int[] colors={0xFFFFFF,0x8CC8FF,0x8CFFB0,0xFFD166,0xFF8CA1,0xD6A4FF};int n=0;for(int i=0;i<colors.length;i++)if(colors[i]==config.hudColor)n=(i+1)%colors.length;config.hudColor=colors[n];config.save();}).dimensions(x,y+50,210,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"),b->client.setScreen(parent)).dimensions(x,y+75,210,20).build());
    }
    @Override public void render(DrawContext c,int mx,int my,float d){renderBackground(c,mx,my,d);c.drawCenteredTextWithShadow(textRenderer,"XCLIENT SETTINGS",width/2,height/2-100,0xFF8CC8FF);super.render(c,mx,my,d);}
}
