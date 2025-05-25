package pama1234.boxmatica.mixin;

import net.minecraft.util.profiler.ProfilerSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProfilerSystem.class)
public interface IMixinProfilerSystem{
  @Accessor("tickStarted")
  boolean boxmatica_isStarted();
}
