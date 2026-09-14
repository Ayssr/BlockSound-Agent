package wtf.tatp.blocksound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * BlockSound核心功能实现
 *
 * 本类包含暴击和格挡音效的检测逻辑
 * 通过Mixin注入到Minecraft中调用
 *
 * @author TATP (原作者), Claude (1.7.10移植)
 * @version 2.0.0-Agent
 */
public class BlockSoundCore {

    // 配置
    private static boolean critSoundEnabled = true;
    private static boolean blockSoundEnabled = true;
    private static int volume = 100; // 0-100

    // 状态跟踪
    private static long lastCritTime = 0;
    private static long lastBlockTime = 0;
    private static int lastHurtTime = 0;
    private static boolean wasBlocking = false;
    private static long blockStartTime = 0;

    // 冷却时间（毫秒）
    private static final long CRIT_COOLDOWN = 300;
    private static final long BLOCK_COOLDOWN = 300;
    private static final long BLOCK_WINDOW = 500;

    /**
     * 初始化模组
     */
    public static void init() {
        System.out.println("[BlockSound] Core initialized!");
        System.out.println("[BlockSound] Version: 2.0.0-Agent");
        System.out.println("[BlockSound] Crit Sound: " + (critSoundEnabled ? "ENABLED" : "DISABLED"));
        System.out.println("[BlockSound] Block Sound: " + (blockSoundEnabled ? "ENABLED" : "DISABLED"));
    }

    /**
     * 攻击实体时调用（用于检测暴击）
     * 从MixinPlayerControllerMP注入
     */
    public static void onAttackEntity() {
        if (!critSoundEnabled) return;

        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null || mc.thePlayer == null) return;

            EntityPlayerSP player = mc.thePlayer;

            // 暴击条件判断（Minecraft 1.7.10原版逻辑）
            boolean isCritical = !player.onGround &&           // 不在地面
                                player.fallDistance == 0.0F &&  // 没有下落距离
                                !player.isInWater() &&          // 不在水中
                                !player.isRiding();             // 不在骑乘

            if (isCritical) {
                long now = System.currentTimeMillis();
                if (now - lastCritTime > CRIT_COOLDOWN) {
                    playCritSound(player);
                    lastCritTime = now;
                }
            }
        } catch (Exception e) {
            System.err.println("[BlockSound] Error in onAttackEntity: " + e.getMessage());
        }
    }

    /**
     * 每tick更新时调用（用于检测格挡）
     * 从MixinEntityPlayerSP注入
     */
    public static void onPlayerUpdate() {
        if (!blockSoundEnabled) return;

        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null || mc.thePlayer == null || mc.theWorld == null) return;

            EntityPlayerSP player = mc.thePlayer;

            // 获取当前状态
            boolean isBlocking = isPlayerBlocking(player);
            int currentHurtTime = player.hurtTime;
            long now = System.currentTimeMillis();

            // 记录格挡开始时间
            if (isBlocking && !wasBlocking) {
                blockStartTime = now;
            }

            // 检测受伤（hurtTime从低变高）
            boolean justHurt = currentHurtTime > lastHurtTime && currentHurtTime > 0;

            if (justHurt) {
                // 智能判断：当前正在格挡 或 最近500ms内格挡过
                boolean likelyBlocked = isBlocking || (now - blockStartTime < BLOCK_WINDOW);
                boolean cooldownPassed = (now - lastBlockTime) > BLOCK_COOLDOWN;

                if (likelyBlocked && cooldownPassed) {
                    playBlockSound(player);
                    lastBlockTime = now;
                }
            }

            // 更新状态
            lastHurtTime = currentHurtTime;
            wasBlocking = isBlocking;

        } catch (Exception e) {
            System.err.println("[BlockSound] Error in onPlayerUpdate: " + e.getMessage());
        }
    }

    /**
     * 判断玩家是否正在格挡
     */
    private static boolean isPlayerBlocking(EntityPlayerSP player) {
        try {
            // 1.7.10中，isBlocking()方法应该存在
            return player.isBlocking();
        } catch (Exception e) {
            // 如果方法不存在，返回false
            return false;
        }
    }

    /**
     * 播放暴击音效
     */
    private static void playCritSound(EntityPlayerSP player) {
        try {
            float vol = volume / 100.0F;
            // 使用经验球音效，音调稍高
            player.playSound("random.orb", vol, 1.5F);
        } catch (Exception e) {
            System.err.println("[BlockSound] Error playing crit sound: " + e.getMessage());
        }
    }

    /**
     * 播放格挡音效
     */
    private static void playBlockSound(EntityPlayerSP player) {
        try {
            float vol = volume / 100.0F;
            // 使用铁砧音效
            player.playSound("random.anvil_use", vol, 1.0F);
        } catch (Exception e) {
            System.err.println("[BlockSound] Error playing block sound: " + e.getMessage());
        }
    }

    // ==================== 配置方法 ====================

    public static void setCritSoundEnabled(boolean enabled) {
        critSoundEnabled = enabled;
        System.out.println("[BlockSound] Crit sound: " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public static void setBlockSoundEnabled(boolean enabled) {
        blockSoundEnabled = enabled;
        System.out.println("[BlockSound] Block sound: " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public static void setVolume(int vol) {
        volume = Math.max(0, Math.min(100, vol));
        System.out.println("[BlockSound] Volume: " + volume);
    }

    public static boolean isCritSoundEnabled() {
        return critSoundEnabled;
    }

    public static boolean isBlockSoundEnabled() {
        return blockSoundEnabled;
    }

    public static int getVolume() {
        return volume;
    }
}
