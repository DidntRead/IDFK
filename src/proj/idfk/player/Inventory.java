package proj.idfk.player;

public class Inventory {
    private Stack[] inventory;
    private float[] vertexData;

    public Inventory() {
        this.inventory = new Stack[40]; // 30 - Inventory, 10 - Hotbar
    }

    private int getFreeIndex() {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                return i;
            }
        }
        return -1;
    }

    private Stack getByID(int id) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                if (inventory[i].id == id && inventory[i].count < 64) {
                    return inventory[i];
                }
            }
        }
        return null;
    }


    public void drop(int id, int count) {
        Stack stack = getByID(id);

        while (count > 0) {
            if (stack.count >= count) {
                stack.count -= count;
                break;
            } else {
                count -= stack.count;
                stack = null;
                stack = getByID(id);
            }
        }
    }

    public int pickup(int id, int count) {
        Stack stack = getByID(id);

        if (stack == null) {
            int index = getFreeIndex();
            if (index == -1) {
                return 0;
            } else {
                inventory[index] = new Stack();
                inventory[index].id = id;
                inventory[index].count = count;
            }
        } else {
            stack.count += count;
            while (stack.count > 64) {
                if (stack.count > 128) {
                    int index = getFreeIndex();
                    if (index == -1) {
                        stack.count = 64;
                        return 64;
                    } else {
                        inventory[index] = new Stack();
                        inventory[index].id = id;
                        inventory[index].count = 64;
                        stack.count -= 64;
                    }
                } else {
                    int c = Math.abs(64 - stack.count);
                    int index = getFreeIndex();
                    if (index == -1) {
                        stack.count = 64;
                        return 64;
                    } else {
                        inventory[index] = new Stack();
                        inventory[index].id = id;
                        inventory[index].count = c;
                        stack.count -= c;
                    }
                }
            }
        }
        return count;
    }
}
