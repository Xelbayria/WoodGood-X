package net.mehvahdjukaar.wood_good.modules.another_furniture;

import com.crispytwig.another_furniture.AnotherFurnitureMod;
import com.crispytwig.another_furniture.block.ChairBlock;
import com.crispytwig.another_furniture.block.TableBlock;
import com.crispytwig.another_furniture.render.ShelfRenderer;
import net.mehvahdjukaar.selene.block_set.wood.WoodType;
import net.mehvahdjukaar.selene.client.asset_generators.LangBuilder;
import net.mehvahdjukaar.selene.client.asset_generators.textures.Palette;
import net.mehvahdjukaar.selene.client.asset_generators.textures.Respriter;
import net.mehvahdjukaar.selene.client.asset_generators.textures.TextureImage;
import net.mehvahdjukaar.selene.resourcepack.DynamicLanguageManager;
import net.mehvahdjukaar.selene.resourcepack.RPUtils;
import net.mehvahdjukaar.selene.resourcepack.ResType;
import net.mehvahdjukaar.wood_good.WoodGood;
import net.mehvahdjukaar.wood_good.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.wood_good.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.wood_good.modules.CompatModule;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

public class AnotherFurnitureModule extends CompatModule {

    public AnotherFurnitureModule(String modId) {
        super(modId);
    }

    @Override
    public String shortenedId() {
        return "af";
    }

    public static final String TABLE_NAME = "table";
    public static final Map<WoodType, Block> TABLES = new HashMap<>();
    public static final Map<WoodType, Item> TABLE_TIMES = new HashMap<>();

    public static final String CHAIR_NAME = "chair";
    public static final Map<WoodType, Block> CHAIRS = new HashMap<>();
    public static final Map<WoodType, Item> CHAIR_ITEMS = new HashMap<>();

    public static final String SHELF_NAME = "shelf";
    public static final Map<WoodType, Block> SHELVES = new HashMap<>();
    public static final Map<WoodType, Item> SHELF_ITEMS = new HashMap<>();

    public static BlockEntityType<CompatShelfBlockTile> COMPAT_SHELF_TILE = null;

    @Override
    public void registerWoodBlocks(IForgeRegistry<Block> registry, Collection<WoodType> woodTypes) {

        //tables
        addChildToOak(shortenedId() + "/table", "oak_table");
        for (WoodType w : woodTypes) {
            String name = makeBlockId(w, TABLE_NAME);
            if (w.isVanilla() || !shouldRegisterEntry(name, registry)) continue;

            Block block = new TableBlock(BlockBehaviour.Properties.copy(w.planks).strength(2.0F, 3.0F));
            TABLES.put(w, block);
            registry.register(block.setRegistryName(WoodGood.res(name)));
            w.addChild(shortenedId() + "/table", block);
        }
        //chair
        addChildToOak(shortenedId() + "/chair", "oak_chair");
        for (WoodType w : woodTypes) {
            String name = makeBlockId(w, CHAIR_NAME);
            if (w.isVanilla() || !shouldRegisterEntry(name, registry)) continue;

            Block block = new ChairBlock(BlockBehaviour.Properties.copy(w.planks).strength(2.0F, 3.0F));
            CHAIRS.put(w, block);
            registry.register(block.setRegistryName(WoodGood.res(name)));
            w.addChild(shortenedId() + "/chair", block);
        }
        //shelf
        addChildToOak(shortenedId() + "/shelf", "oak_shelf");
        for (WoodType w : woodTypes) {
            String name = makeBlockId(w, SHELF_NAME);
            if (w.isVanilla() || !shouldRegisterEntry(name, registry)) continue;

            Block block = new CompatShelfBlock(BlockBehaviour.Properties.copy(w.planks).strength(2.0F, 3.0F));
            SHELVES.put(w, block);
            registry.register(block.setRegistryName(WoodGood.res(name)));
            w.addChild(shortenedId() + "/shelf", block);
        }
    }

    @Override
    public void registerItems(IForgeRegistry<Item> registry) {
        TABLES.forEach((key, value) -> {
            Item i = new BlockItem(value, new Item.Properties().tab(AnotherFurnitureMod.TAB));
            TABLE_TIMES.put(key, i);
            registry.register(i.setRegistryName(value.getRegistryName()));
        });
        CHAIRS.forEach((key, value) -> {
            Item i = new BlockItem(value, new Item.Properties().tab(AnotherFurnitureMod.TAB));
            CHAIR_ITEMS.put(key, i);
            registry.register(i.setRegistryName(value.getRegistryName()));
        });
        SHELVES.forEach((key, value) -> {
            Item i = new BlockItem(value, new Item.Properties().tab(AnotherFurnitureMod.TAB));
            SHELF_ITEMS.put(key, i);
            registry.register(i.setRegistryName(value.getRegistryName()));
        });
    }

    @Override
    public void registerTiles(IForgeRegistry<BlockEntityType<?>> registry) {
        COMPAT_SHELF_TILE = BlockEntityType.Builder.of(CompatShelfBlockTile::new,
                SHELVES.values().toArray(Block[]::new)).build(null);
        registry.register(COMPAT_SHELF_TILE.setRegistryName("af_shelf"));
    }

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        CHAIRS.values().forEach(t -> ItemBlockRenderTypes.setRenderLayer(t, RenderType.cutout()));
    }

    @Override
    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(COMPAT_SHELF_TILE, ShelfRenderer::new);
    }

    @Override
    public void addStaticServerResources(ServerDynamicResourcesHandler handler, ResourceManager manager) {
        var pack = handler.dynamicPack;
        List<ResourceLocation> tables = new ArrayList<>();
        TABLES.forEach((wood, value) -> {
            pack.addSimpleBlockLootTable(value);
            tables.add(value.getRegistryName());
        });
        pack.addTag(modRes("tables"), tables, Registry.BLOCK_REGISTRY);
        pack.addTag(modRes("tables"), tables, Registry.ITEM_REGISTRY);

        List<ResourceLocation> chairs = new ArrayList<>();
        CHAIRS.forEach((wood, value) -> {
            pack.addSimpleBlockLootTable(value);
            chairs.add(value.getRegistryName());
        });
        pack.addTag(modRes("chairs"), chairs, Registry.BLOCK_REGISTRY);
        pack.addTag(modRes("chairs"), chairs, Registry.ITEM_REGISTRY);

        List<ResourceLocation> shelves = new ArrayList<>();
        SHELVES.forEach((wood, value) -> {
            pack.addSimpleBlockLootTable(value);
            shelves.add(value.getRegistryName());
        });
        pack.addTag(modRes("shelves"), shelves, Registry.BLOCK_REGISTRY);
        pack.addTag(modRes("shelves"), shelves, Registry.ITEM_REGISTRY);
    }

    @Override
    public void addDynamicServerResources(ServerDynamicResourcesHandler handler, ResourceManager manager) {
        this.addBlocksRecipes(manager, handler, TABLES, "oak_table");
        this.addBlocksRecipes(manager, handler, TABLES, "oak_shelf");
        this.addBlocksRecipes(manager, handler, TABLES, "oak_chair");
    }

    @Override
    public void addStaticClientResources(ClientDynamicResourcesHandler handler, ResourceManager manager) {

        this.addBlockResources(manager, handler, TABLES,
                WoodJsonTransformation.create(modId, manager)
                        .replaceWoodInPath("table/oak")
                        .replaceWoodBlock("table/oak"),
                ResType.BLOCK_MODELS.getPath(modRes("table/oak_leg")),
                ResType.BLOCK_MODELS.getPath(modRes("table/oak_top"))
        );
        this.addBlockResources(manager, handler, TABLES,
                WoodJsonTransformation.create(modId, manager)
                        .replaceWoodInPath("oak")
                        .replaceWoodBlock("table/oak"),
                ResType.ITEM_MODELS.getPath(modRes("oak_table")),
                ResType.BLOCKSTATES.getPath(modRes("oak_table"))
        );


        this.addBlockResources(manager, handler, CHAIRS,
                WoodJsonTransformation.create(modId, manager)
                        .replaceWoodInPath("chair/oak")
                        .replaceWoodBlock("chair/oak"),
                ResType.BLOCK_MODELS.getPath(modRes("chair/oak"))
        );
        this.addBlockResources(manager, handler, CHAIRS,
                WoodJsonTransformation.create(modId, manager)
                        .replaceWoodInPath("oak")
                        .replaceWoodBlock("chair/oak"),
                ResType.BLOCKSTATES.getPath(modRes("oak_chair")),
                ResType.ITEM_MODELS.getPath(modRes("oak_chair"))
        );

        this.addBlockResources(manager, handler, SHELVES,
                WoodJsonTransformation.create(modId, manager)
                        .replaceWoodInPath("shelf/oak")
                        .replaceWoodBlock("shelf/oak"),
                ResType.BLOCK_MODELS.getPath(modRes("shelf/oak_full")),
                ResType.BLOCK_MODELS.getPath(modRes("shelf/oak_r")),
                ResType.BLOCK_MODELS.getPath(modRes("shelf/oak_l")),
                ResType.BLOCK_MODELS.getPath(modRes("shelf/oak_top"))
        );
        this.addBlockResources(manager, handler, SHELVES,
                WoodJsonTransformation.create(modId, manager)
                        .replaceWoodInPath("oak")
                        .replaceWoodBlock("shelf/oak"),
                ResType.BLOCKSTATES.getPath(modRes("oak_shelf")),
                ResType.ITEM_MODELS.getPath(modRes("oak_shelf"))
        );
    }

    @Override
    public void addTranslations(ClientDynamicResourcesHandler clientDynamicResourcesHandler, DynamicLanguageManager.LanguageAccessor lang) {
        TABLES.forEach((w, v) -> LangBuilder.addDynamicEntry(lang, "block.wood_good.table", w, v));
        CHAIRS.forEach((w, v) -> LangBuilder.addDynamicEntry(lang, "block.wood_good.chair", w, v));
        SHELVES.forEach((w, v) -> LangBuilder.addDynamicEntry(lang, "block.wood_good.shelf", w, v));
    }


    @Override
    public void addDynamicClientResources(ClientDynamicResourcesHandler handler, ResourceManager manager) {
        var pack = handler.dynamicPack;

        //tables
        try (TextureImage sides = TextureImage.open(manager,
                modRes("block/table/oak_sides"));
             TextureImage top = TextureImage.open(manager,
                     modRes("block/table/oak_top"))) {

            Respriter respriterSides = Respriter.of(sides);
            Respriter respriterTop = Respriter.of(top);

            TABLES.forEach((wood, table) -> {

                String id = table.getRegistryName().getPath();

                try (TextureImage plankTexture = TextureImage.open(manager,
                        RPUtils.findFirstBlockTextureLocation(manager, wood.planks))) {

                    List<Palette> targetPalette = Palette.fromAnimatedImage(plankTexture);

                    handler.addTextureIfNotPresent(manager, "block/" + id.replace("table", "sides"), () ->
                            respriterSides.recolorWithAnimation(targetPalette, plankTexture.getMetadata()));

                    handler.addTextureIfNotPresent(manager, "block/" + id.replace("table", "top"), () ->
                            respriterTop.recolorWithAnimation(targetPalette, plankTexture.getMetadata()));


                } catch (Exception ex) {
                    handler.getLogger().error("Failed to generate Table block texture for for {} : {}", table, ex);
                }
            });
        } catch (Exception ex) {
            handler.getLogger().error("Could not generate any Table block texture : ", ex);
        }
        //TODO: write smething that grabs the blockstate and models automatically
        //chairs
        try (TextureImage back = TextureImage.open(manager,
                modRes("block/chair/oak_back"));
             TextureImage bottom = TextureImage.open(manager,
                     modRes("block/chair/oak_bottom"));
             TextureImage seat = TextureImage.open(manager,
                     modRes("block/chair/oak_seat"))) {

            Respriter respriterBack = Respriter.of(back);
            Respriter respriterBottom = Respriter.of(bottom);
            Respriter respriterSeat = Respriter.of(seat);

            CHAIRS.forEach((wood, table) -> {

                String id = table.getRegistryName().getPath();

                try (TextureImage plankTexture = TextureImage.open(manager,
                        RPUtils.findFirstBlockTextureLocation(manager, wood.planks))) {

                    List<Palette> targetPalette = Palette.fromAnimatedImage(plankTexture);

                    handler.addTextureIfNotPresent(manager, "block/" + id.replace("chair", "back"), () ->
                            respriterBack.recolorWithAnimation(targetPalette, plankTexture.getMetadata()));

                    handler.addTextureIfNotPresent(manager, "block/" + id.replace("chair", "bottom"), () ->
                            respriterBottom.recolorWithAnimation(targetPalette, plankTexture.getMetadata()));

                    handler.addTextureIfNotPresent(manager, "block/" + id.replace("chair", "seat"), () ->
                            respriterSeat.recolorWithAnimation(targetPalette, plankTexture.getMetadata()));

                } catch (Exception ex) {
                    handler.getLogger().error("Failed to generate Chair block texture for for {} : {}", table, ex);
                }
            });
        } catch (Exception ex) {
            handler.getLogger().error("Could not generate any Chair block texture : ", ex);
        }
        //shelves
        try (TextureImage supports = TextureImage.open(manager,
                modRes("block/shelf/oak_supports"))) {

            Respriter respriter = Respriter.of(supports);

            SHELVES.forEach((wood, table) -> {

                String id = table.getRegistryName().getPath();

                try (TextureImage plankTexture = TextureImage.open(manager,
                        RPUtils.findFirstBlockTextureLocation(manager, wood.planks))) {

                    List<Palette> targetPalette = Palette.fromAnimatedImage(plankTexture);

                    handler.addTextureIfNotPresent(manager, "block/" + id.replace("shelf", "supports"), () ->
                            respriter.recolorWithAnimation(targetPalette, plankTexture.getMetadata()));

                } catch (Exception ex) {
                    handler.getLogger().error("Failed to generate Shelf block texture for for {} : {}", table, ex);
                }
            });
        } catch (Exception ex) {
            handler.getLogger().error("Could not generate any Shelf block texture : ", ex);
        }
    }
}
