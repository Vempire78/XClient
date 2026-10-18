package dev.xclient;

import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class XClientScreen extends Screen {
    private final ModuleManager modules; private final XClientConfig config;
    private String category="All"; private TextFieldWidget search; private int scroll;
    public XClientScreen(ModuleManager modules,XClientConfig config){super(Text.literal("XClient"));this.modules=modules;this.config=config;}
    @Override protected void init(){
        int left=Math.max(12,width/2-150),top=Math.max(8,height/2-120);
        search=new TextFieldWidget(textRenderer,left+10,top+32,190,18,Text.literal("Search"));
        search.setPlaceholder(Text.literal("Search modules..."));addDrawableChild(search);
        String[] cats={"All","HUD","SMP"};
        for(int i=0;i<cats.length;i++){String cat=cats[i];addDrawableChild(ButtonWidget.builder(Text.literal(cat),b->{category=cat;scroll=0;clearAndInit();}).dimensions(left+10+i*64,top+54,60,18).build());}
        addDrawableChild(ButtonWidget.builder(Text.literal("Settings / Presets"),b->client.setScreen(new ClientHubScreen(modules,config,this))).dimensions(left+10,top+78,190,20).build());
        int y=top+104,index=0;
        for(ClientModule m:modules.all()){
            if(!category.equals("All")&&!m.category().equals(category))continue;
            if(search!=null&&!m.name().toLowerCase(Locale.ROOT).contains(search.getText().toLowerCase(Locale.ROOT)))continue;
            if(index++<scroll)continue;if(y>top+196)break;
            ClientModule module=m;
            addDrawableChild(ButtonWidget.builder(Text.literal(module.name()+"  "+(module.enabled()?"[ON]":"[OFF]")),b->{module.toggle();modules.save(config);clearAndInit();}).dimensions(left+10,y,190,18).build());y+=20;
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"),b->close()).dimensions(left+140,top+218,60,18).build());
    }
    @Override public void render(DrawContext c,int mx,int my,float d){
        renderBackground(c,mx,my,d);int l=Math.max(12,width/2-150),t=Math.max(8,height/2-120);
        c.fill(l,t,l+210,t+244,0xEE11151D);c.fill(l,t,l+210,t+25,0xFF242B3A);
        c.drawText(textRenderer,"XCLIENT",l+10,t+8,0xFF8CC8FF,true);super.render(c,mx,my,d);
    }
    @Override public boolean mouseScrolled(double x,double y,double h,double v){scroll=Math.max(0,Math.min(modules.all().size(),scroll+(v<0?1:-1)));clearAndInit();return true;}
    @Override public void close(){modules.save(config);config.save();super.close();}
}
