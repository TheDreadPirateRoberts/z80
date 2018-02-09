package co.uk.dreadpirateroberts.z80;

import javax.naming.ldap.Control;
import java.util.Observable;
import java.util.Observer;

public class MemoryController implements Observer
{
    private AddressBus addressBus;
    private MemoryBus memoryBus;
    private ControlBus controlBus;
    private byte[] memory = new byte[1024];

    public MemoryController(AddressBus ab, MemoryBus mb, ControlBus cb)
    {
        addressBus = ab;
        memoryBus = mb;
        controlBus = cb;
        memory[0] = (byte)62;
        memory[1] = (byte)25;
        memory[2] = (byte)71;
        memory[3] = (byte)118;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if(controlBus.MREQ)
        {
            if(controlBus.RD)
            {
                memoryBus.data = memory[addressBus.get()];
                controlBus.RD = false;
                controlBus.MREQ = false;
            }
        }
    }
}
