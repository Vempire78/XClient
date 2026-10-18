
package dev.xclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {
    private final XClientConfig config;private final Screen parent;private String dragging="";
    public HudEditorScreen(XClientConfig c,Screen p){super(Text.literal("XClient HUD Editor"));config=c;parent=p;}
    @Override public void render(DrawContext c,int mx,int my,float d){
        renderBackground(c,mx,my,d);c.fill(0,0,width,height,0x66000000);
        c.drawCenteredTextWithShadow(textRenderer,"HUD EDITOR — drag labels to move",width/2,10,0xFFFFFFFF);
        c.drawText(textRenderer,"FPS: 144",config.fpsX,config.fpsY,config.hudColor,true);
        c.drawText(textRenderer,"XYZ: 120 64 -35",config.coordsX,config.coordsY,config.hudColor,true);
        c.drawText(textRenderer,"Armor Status",config.armorX,config.armorY,config.hudColor,true);
        c.drawText(textRenderer,"Potion Effects",config.potionX,config.potionY,config.hudColor,true);
        c.drawText(textRenderer,"Durability",config.durabilityX,config.durabilityY,config.hudColor,true);
        c.drawText(textRenderer,"W A S D  |  Keystrokes",config.keysX,config.keysY,config.hudColor,true);
        c.drawText(textRenderer,"Server: example.net",config.infoX,config.infoY,config.hudColor,true);
        c.drawCenteredTextWithShadow(textRenderer,"ESC to return",width/2,height-18,0xFFCCCCCC);super.render(c,mx,my,d);
    }
    @Override public boolean mouseClicked(double mx,double my,int b){
        if(b==0){if(hit(mx,my,config.fpsX,config.fpsY,65))dragging="fps";else if(hit(mx,my,config.coordsX,config.coordsY,120))dragging="coords";else if(hit(mx,my,config.armorX,config.armorY,100))dragging="armor";else if(hit(mx,my,config.potionX,config.potionY,110))dragging="potion";else if(hit(mx,my,config.durabilityX,config.durabilityY,100))dragging="durability";else if(hit(mx,my,config.keysX,config.keysY,150))dragging="keys";else if(hit(mx,my,config.infoX,config.infoY,150))dragging="info";return true;}return super.mouseClicked(mx,my,b);
    }
    private boolean hit(double mx,double my,int x,int y,int w){return mx>=x&&mx<=x+w&&my>=y-3&&my<=y+12;}
    @Override public boolean mouseDragged(double mx,double my,int b,double dx,double dy){
        if(!dragging.isEmpty()){int x=(int)mx,y=(int)my;switch(dragging){case "fps"->{config.fpsX=x;config.fpsY=y;}case "coords"->{config.coordsX=x;config.coordsY=y;}case "armor"->{config.armorX=x;config.armorY=y;}case "potion"->{config.potionX=x;config.potionY=y;}case "durability"->{config.durabilityX=x;config.durabilityY=y;}case "keys"->{config.keysX=x;config.keysY=y;}case "info"->{config.infoX=x;config.infoY=y;}}config.save();return true;}return super.mouseDragged(mx,my,b,dx,dy);
    }
    @Override public boolean mouseReleased(double x,double y,int b){dragging="";return super.mouseReleased(x,y,b);}
    @Override public void close(){config.save();client.setScreen(parent);}
}
