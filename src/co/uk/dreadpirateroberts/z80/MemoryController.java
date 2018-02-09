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
        memory[0] = (byte) 0x3e;       // LD   A,25
        memory[1] = (byte) 0x19;
        memory[2] = (byte) 0x47;       // LD   B,A
        //memory[3] = (byte) 0x53;       // ADD  A,B
        memory[3] = (byte) 0x32;       // LD   (007FH),A
        memory[4] = (byte) 0x7f;
        memory[5] = (byte) 0x0;
        memory[6] = (byte) 0x76;      // HALT
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
            else if(controlBus.WR)
            {
                memory[addressBus.get()] = memoryBus.data;
                controlBus.WR = false;
                controlBus.MREQ = false;
            }
        }
    }

    public void dumpMemory()
    {
        for (int i = 0; i < memory.length; i++)
        {
            if(memory[i] != 0)
            {
                System.out.println("[" + i + "] : " + memory[i]);
            }
        }
    }
}
