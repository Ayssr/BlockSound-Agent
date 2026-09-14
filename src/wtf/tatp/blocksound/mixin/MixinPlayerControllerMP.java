package wtf.tatp.blocksound.mixin;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.tatp.blocksound.BlockSoundCore;

/**
 * Mixin注入到PlayerControllerMP
 * 用于检测玩家攻击实体事件
 *
 * @author TATP (原作者), Claude (1.7.10移植)
 */
@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    /**
     * 注入到attackEntity方法
     * 在方法开始处调用我们的暴击检测
     */
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(EntityPlayer player, Entity target, CallbackInfo ci) {
        // 调用暴击检测
        BlockSoundCore.onAttackEntity();
    }
}
