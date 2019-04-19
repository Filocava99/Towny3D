package it.tigierrei.towny3d.regions;

public class Region {

    private int y1;
    private int y2;

    private String owner;
    private String town;

    private boolean for_sale;
    private double value;

    private String type;
    private String name;

    //Inizio perm
    private boolean res_pvp;
    private boolean res_inventory;
    private boolean res_interact;
    private boolean res_build;
    private boolean res_destroy;
    private boolean res_drop;
    private boolean res_pick;
    private boolean res_move;

    private boolean ally_pvp;
    private boolean ally_inventory;
    private boolean ally_interact;
    private boolean ally_build;
    private boolean ally_destroy;
    private boolean ally_drop;
    private boolean ally_pick;
    private boolean ally_move;

    private boolean gen_pvp;
    private boolean gen_inventory;
    private boolean gen_interact;
    private boolean gen_build;
    private boolean gen_destroy;
    private boolean gen_drop;
    private boolean gen_pick;
    private boolean gen_move;
    //Fine perm

    //Toggle
    private boolean fire;
    private boolean explosion;
    private boolean friendly_mobs_spawn;
    private boolean hostile_mobs_spawn;

    public Region(int y1, int y2, String owner, String town, boolean for_sale, double value, String type, String name, boolean fire, boolean explosion, boolean res_pvp, boolean res_inventory, boolean res_interact, boolean res_build, boolean res_destroy, boolean res_drop, boolean res_pick, boolean res_move, boolean ally_pvp, boolean ally_inventory, boolean ally_interact, boolean ally_build, boolean ally_destroy, boolean ally_drop, boolean ally_pick, boolean ally_move, boolean gen_pvp, boolean gen_inventory, boolean gen_interact, boolean gen_build, boolean gen_destroy, boolean gen_drop, boolean gen_pick, boolean gen_move, boolean friendly_mobs_spawn, boolean hostile_mobs_spawn) {
        this.y1 = y1;
        this.y2 = y2;
        this.owner = owner;
        this.town = town;
        this.for_sale = for_sale;
        this.value = value;
        this.type = type;
        this.name = name;
        this.fire = fire;
        this.explosion = explosion;
        this.res_pvp = res_pvp;
        this.res_inventory = res_inventory;
        this.res_interact = res_interact;
        this.res_build = res_build;
        this.res_destroy = res_destroy;
        this.res_drop = res_drop;
        this.res_pick = res_pick;
        this.res_move = res_move;
        this.ally_pvp = ally_pvp;
        this.ally_inventory = ally_inventory;
        this.ally_interact = ally_interact;
        this.ally_build = ally_build;
        this.ally_destroy = ally_destroy;
        this.ally_drop = ally_drop;
        this.ally_pick = ally_pick;
        this.ally_move = ally_move;
        this.gen_pvp = gen_pvp;
        this.gen_inventory = gen_inventory;
        this.gen_interact = gen_interact;
        this.gen_build = gen_build;
        this.gen_destroy = gen_destroy;
        this.gen_drop = gen_drop;
        this.gen_pick = gen_pick;
        this.gen_move = gen_move;
        this.friendly_mobs_spawn = friendly_mobs_spawn;
        this.hostile_mobs_spawn = hostile_mobs_spawn;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public String getOwner() {
        return owner;
    }

    public String getTown() {
        return town;
    }

    public boolean isFor_sale() {
        return for_sale;
    }

    public double getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isFire() {
        return fire;
    }

    public boolean isExplosion() {
        return explosion;
    }

    public boolean isRes_pvp() {
        return res_pvp;
    }

    public boolean isRes_inventory() {
        return res_inventory;
    }

    public boolean isRes_interact() {
        return res_interact;
    }

    public boolean isRes_build() {
        return res_build;
    }

    public boolean isRes_destroy() {
        return res_destroy;
    }

    public boolean isRes_drop() {
        return res_drop;
    }

    public boolean isRes_pick() {
        return res_pick;
    }

    public boolean isRes_move() {
        return res_move;
    }

    public boolean isAlly_pvp() {
        return ally_pvp;
    }

    public boolean isAlly_inventory() {
        return ally_inventory;
    }

    public boolean isAlly_interact() {
        return ally_interact;
    }

    public boolean isAlly_build() {
        return ally_build;
    }

    public boolean isAlly_destroy() {
        return ally_destroy;
    }

    public boolean isAlly_drop() {
        return ally_drop;
    }

    public boolean isAlly_pick() {
        return ally_pick;
    }

    public boolean isAlly_move() {
        return ally_move;
    }

    public boolean isGen_pvp() {
        return gen_pvp;
    }

    public boolean isGen_inventory() {
        return gen_inventory;
    }

    public boolean isGen_interact() {
        return gen_interact;
    }

    public boolean isGen_build() {
        return gen_build;
    }

    public boolean isGen_destroy() {
        return gen_destroy;
    }

    public boolean isGen_drop() {
        return gen_drop;
    }

    public boolean isGen_pick() {
        return gen_pick;
    }

    public boolean isGen_move() {
        return gen_move;
    }

    public boolean isFriendly_mobs_spawn() {
        return friendly_mobs_spawn;
    }

    public boolean isHostile_mobs_spawn() {
        return hostile_mobs_spawn;
    }
}
