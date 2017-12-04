package prospector.industryreborn.tiles;

public class TileEntityCentrifuge extends TileEntityMachine {
    @Override
    public void update() {
        System.out.println("test" + world.getWorldTime());
    }
}
