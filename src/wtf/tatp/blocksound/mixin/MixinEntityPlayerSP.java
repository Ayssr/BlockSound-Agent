package wtf.tatp.blocksound.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.blocksound.BlockSoundCore;

/**
 * Mixin注入到EntityPlayerSP
 * 用于检测玩家更新事件（格挡检测）
 *
 * @author TATP (原作者), Claude (1.7.10移植)
 */
@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    /**
     * 注入到onUpdate方法
     * 在方法开始处调用我们的格挡检测
     */
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        // 调用格挡检测
        BlockSoundCore.onPlayerUpdate();
    }
}
