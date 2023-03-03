package hungteen.htlib.util.helper;

import hungteen.htlib.util.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:53
 **/
public class EntityHelper extends RegistryHelper<EntityType<?>>{

    private static final EntityHelper HELPER = new EntityHelper();

    public static boolean isMainHolding(LivingEntity entity, Predicate<ItemStack> predicate) {
        return predicate.test(entity.getMainHandItem());
    }

    public static boolean isOffHolding(LivingEntity entity, Predicate<ItemStack> predicate) {
        return predicate.test(entity.getOffhandItem());
    }

    public static boolean isEntityValid(Entity entity) {
        return entity != null && entity.isAlive();
    }

    /**
     * used for bullets hit check.
     * get predicate entity between start to end.
     */
    public static EntityHitResult rayTraceEntities(Level world, Entity entity, Vec3 startVec, Vec3 endVec, Predicate<Entity> predicate) {
        return ProjectileUtil.getEntityHitResult(world, entity, startVec, endVec,
                entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).inflate(0.5D), predicate);
    }

    /**
     * used for item ray trace.
     * get predicate entity with dis and vec.
     */
    public static EntityHitResult rayTraceEntities(Level world, Entity entity, Vec3 lookVec, double distance, Predicate<Entity> predicate) {
        final Vec3 startVec = entity.getEyePosition();
        Vec3 endVec = startVec.add(lookVec.normalize().scale(distance));
        ClipContext ray = new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        BlockHitResult result = world.clip(ray);
        if (result.getType() != HitResult.Type.MISS) {// hit something
            endVec = result.getLocation();
        }
        return ProjectileUtil.getEntityHitResult(world, entity, startVec, endVec,
                entity.getBoundingBox().inflate(distance), predicate);
    }

    /**
     * get AABB by entity's width and height.
     */
    public static AABB getEntityAABB(Entity entity, double radius, double height){
        return MathHelper.getAABB(entity.position(), radius, height);
    }

    /**
     * base predicate target method.
     */
    public static <T extends Entity> List<T> getPredicateEntities(@Nonnull Entity attacker, AABB aabb, Class<T> tClass, Predicate<T> predicate) {
        final IntOpenHashSet set = new IntOpenHashSet();
        final List<T> entities = new ArrayList<>();
        getPredicateEntityWithParts(attacker, aabb, tClass, predicate).forEach(e -> {
            if (!set.contains(e.getId())) {
                set.addAll(getOwnerAndPartsID(e));
                entities.add(e);
            }
        });
        return entities;
    }

    /**
     * base predicate target method.
     */
    public static <T extends Entity> List<T> getPredicateEntityWithParts(@Nonnull Entity attacker, AABB aabb, Class<T> tClass, Predicate<T> predicate) {
        return attacker.level.getEntitiesOfClass(tClass, aabb).stream().filter(target -> {
            return !attacker.equals(target) && predicate.test(target);
        }).collect(Collectors.toList());
    }

    /**
     * get all parts of an entity, so that you can only hit them once.
     */
    public static List<Integer> getOwnerAndPartsID(Entity entity) {
        List<Integer> list = new ArrayList<>();
        if (entity instanceof PartEntity<?>) {
            Entity owner = ((PartEntity<?>) entity).getParent();
            list.add(owner.getId());
            for (PartEntity<?> part : owner.getParts()) {
                list.add(part.getId());
            }
        } else {
            list.add(entity.getId());
        }
        return list;
    }

    /**
     * Get predicate registry objects.
     */
    public static List<EntityType<?>> getFilterEntityTypes(Predicate<EntityType<?>> predicate) {
        return get().getFilterObjects(predicate);
    }

    /**
     * Get all registered objects with keys.
     */
    public static Collection<Pair<ResourceKey<EntityType<?>>, EntityType<?>>> getEntityTypeWithKeys() {
        return get().getObjectWithKeys();
    }

    /**
     * Get key of specific object.
     */
    public static ResourceLocation getKey(EntityType<?> object) {
        return get().getResourceLocation(object);
    }

    public static EntityHelper get(){
        return HELPER;
    }

    @Override
    public IForgeRegistry<EntityType<?>> getForgeRegistry() {
        return ForgeRegistries.ENTITY_TYPES;
    }
}
