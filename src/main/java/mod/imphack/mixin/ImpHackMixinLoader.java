package mod.imphack.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

public class ImpHackMixinLoader implements IFMLLoadingPlugin {
	public ImpHackMixinLoader() {
		MixinBootstrap.init();

		Mixins.addConfiguration("mixins.imphack.json");

		MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");

		System.out.println("ImpHack: loaded mixins");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
