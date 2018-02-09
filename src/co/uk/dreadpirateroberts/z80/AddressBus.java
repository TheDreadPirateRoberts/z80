package co.uk.dreadpirateroberts.z80;

public class AddressBus {
    private int value;

    public void set(int val)
    {
        value = val;
    }

    public int get()
    {
        return value;
    }
}
