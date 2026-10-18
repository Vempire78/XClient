package dev.xclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class XClient implements ClientModInitializer {
    private static final ModuleManager MODULES=new ModuleManager();
    private static final XClientConfig CONFIG=new XClientConfig();
    private static KeyBinding openMenuKey;
    private static int leftCps,rightCps,tick;
    private static long[] leftClicks=new long[32],rightClicks=new long[32];
    private static int leftIndex,rightIndex;

    @Override public void onInitializeClient(){
        CONFIG.load(); MODULES.load(CONFIG);
        openMenuKey=KeyBindingHelper.registerKeyBinding(new KeyBinding("key.xclient.open_menu",InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_RIGHT_SHIFT,KeyBinding.MISC_CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            while(openMenuKey.wasPressed())if(client.currentScreen==null)client.setScreen(new XClientScreen(MODULES,CONFIG));
            tick++; if(tick%10==0)updateCps();
        });
        HudRenderCallback.EVENT.register((ctx,counter)->renderHud(ctx));
    }
    private static void updateCps(){
        long now=System.currentTimeMillis();leftCps=count(leftClicks,now);rightCps=count(rightClicks,now);
    }
    private static int count(long[] a,long now){int n=0;for(long t:a)if(now-t<1000)n++;return n;}
    public static void registerClick(boolean left){
        long now=System.currentTimeMillis();
        if(left){leftClicks[leftIndex++%leftClicks.length]=now;}else{rightClicks[rightIndex++%rightClicks.length]=now;}
    }
    private void renderHud(DrawContext c){
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.player==null||mc.options.hudHidden)return;
        int col=0xFF000000|(CONFIG.hudColor&0xFFFFFF);
        if(MODULES.isEnabled("FPS"))text(c,"FPS: "+mc.getCurrentFps(),CONFIG.fpsX,CONFIG.fpsY,col);
        if(MODULES.isEnabled("Coordinates"))text(c,String.format("XYZ: %.1f %.1f %.1f",mc.player.getX(),mc.player.getY(),mc.player.getZ()),CONFIG.coordsX,CONFIG.coordsY,col);
        if(MODULES.isEnabled("Armor Status"))renderArmor(c,mc, col);
        if(MODULES.isEnabled("Potion Effects"))renderPotions(c,mc,col);
        if(MODULES.isEnabled("Durability"))renderDurability(c,mc,col);
        if(MODULES.isEnabled("Keystrokes"))renderKeys(c,mc,col);
        if(MODULES.isEnabled("SMP Info"))renderInfo(c,mc,col);
    }
    private void text(DrawContext c,String s,int x,int y,int col){c.drawText(MinecraftClient.getInstance().textRenderer,Text.literal(s),x,y,col,true);}
    private void renderArmor(DrawContext c,MinecraftClient mc,int col){
        int y=CONFIG.armorY;
        for(ItemStack stack:mc.player.getArmorItems()){
            int x=CONFIG.armorX;
            c.drawItem(stack,x,y-2);
            String name=stack.isEmpty()?"Empty":stack.getName().getString();
            String durability=stack.isEmpty()?"":(" "+(stack.getMaxDamage()-stack.getDamage())+"/"+stack.getMaxDamage());
            text(c,name+durability,x+20,y,col);y+=18;
        }
    }
    private void renderPotions(DrawContext c,MinecraftClient mc,int col){
        int y=CONFIG.potionY;
        for(StatusEffectInstance effect:mc.player.getStatusEffects()){
            String name=effect.getEffectType().value().getName().getString();
            int seconds=effect.getDuration()/20;
            String timer=String.format("%d:%02d",seconds/60,seconds%60);
            String level=effect.getAmplifier()>0?" "+(effect.getAmplifier()+1):"";
            String line=name+level+"  "+timer;
            if("JoZo".equals(CONFIG.potionPreset))line="["+timer+"] "+name+level;
            else if("Lunar".equals(CONFIG.potionPreset))line=name+level+"  §7"+timer;
            else if("Feather".equals(CONFIG.potionPreset))line="◆ "+name+level+"  "+timer;
            text(c,line,CONFIG.potionX,y,col);y+=10;
        }
    }
    private void renderDurability(DrawContext c,MinecraftClient mc,int col){
        ItemStack s=mc.player.getMainHandStack();
        if(!s.isEmpty()){
            String line=s.getName().getString();
            if(s.isDamageable())line+=" "+(s.getMaxDamage()-s.getDamage())+"/"+s.getMaxDamage();
            text(c,line,CONFIG.durabilityX,CONFIG.durabilityY,col);
        }
    }
    private void renderKeys(DrawContext c,MinecraftClient mc,int col){
        int x=CONFIG.keysX,y=CONFIG.keysY;
        String w=key(mc.options.forwardKey.isPressed(),"W"),a=key(mc.options.leftKey.isPressed(),"A"),
            s=key(mc.options.backKey.isPressed(),"S"),d=key(mc.options.rightKey.isPressed(),"D");
        text(c,"   ["+w+"]",x,y,col);text(c,"["+a+"] ["+s+"] ["+d+"]",x,y+12,col);
        text(c,"Jump: "+key(mc.options.jumpKey.isPressed(),"ON")+"  Sneak: "+key(mc.options.sneakKey.isPressed(),"ON"),x,y+24,col);
        text(c,"LMB CPS: "+leftCps+"  RMB CPS: "+rightCps,x,y+36,col);
    }
    private String key(boolean pressed,String label){return pressed?"§a"+label+"§r":label;}
    private void renderInfo(DrawContext c,MinecraftClient mc,int col){
        String server=mc.getCurrentServerEntry()==null?"Singleplayer":mc.getCurrentServerEntry().address;
        text(c,"Server: "+server,CONFIG.infoX,CONFIG.infoY,col);
        if(mc.getNetworkHandler()!=null&&mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid())!=null)
            text(c,"Ping: "+mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency()+" ms",CONFIG.infoX,CONFIG.infoY+12,col);
    }
    public static XClientConfig config(){return CONFIG;}
}
