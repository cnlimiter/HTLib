package hungteen.htlib.data.tag;

import hungteen.htlib.util.EntityUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:52
 **/
public class HTEntityTagGen extends EntityTypeTagsProvider {

    public HTEntityTagGen(DataGenerator p_126517_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126517_, modId, existingFileHelper);
    }

    protected List<EntityType<?>> getFilterTypes(Predicate<EntityType<?>> predicate) {
        return EntityUtil.getFilterTypes(predicate);
    }

    @Override
    public String getName() {
        return this.modId + " entity type tags";
    }

}
