package co.uk.dreadpirateroberts.z80;

import java.util.Observable;
import java.util.Observer;

import static java.lang.Thread.sleep;

public class InstructionDecoder implements Observer {
    public State state = State.Idle;

    private Registers registers;
    private AddressBus addressBus;
    private ControlBus controlBus;
    private MemoryBus memoryBus;
    private byte ir = 0;
    private byte tempRegister1 = 0;
    private byte tempRegister2 = 0;


    public InstructionDecoder(Registers r, AddressBus ab, ControlBus cb, MemoryBus mb)
    {
        registers = r;
        addressBus = ab;
        controlBus = cb;
        memoryBus = mb;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if(state == State.HALTING)
            return;

        printState();
        if (controlBus.M1)
        {
            state = State.InstructionFetch;
            fetchInstruction();
            controlBus.M1 = false;
        }
        else
        {
            switch(state) {
                case HALTING:
                    break;
                case InstructionFetch:
                    state = State.InstructionRead;
                    readInstruction();
                    break;
                case InstructionRead:
                    state = State.InstructionDecode;
                    decodeInstruction();
                    break;
                case DataRead:
                case DataRead2:
                    decodeInstruction();
                    break;
            }
        }
    }

    private void fetchInstruction()
    {
        addressBus.set(registers.PC);
        controlBus.MREQ = true;
        controlBus.RD = true;
    }

    private void readInstruction()
    {
        ir = memoryBus.data;
        registers.PC++;
    }

    private void decodeInstruction()
    {
        switch(ir)
        {
            case 0x32: // LD  (nn),A
                if(state == State.InstructionDecode)
                {
                    addressBus.set(registers.PC);
                    controlBus.MREQ = true;
                    controlBus.RD = true;
                    state = State.DataRead;
                }
                else if (state == State.DataRead)
                {
                    tempRegister1 = memoryBus.data;
                    registers.PC++;
                    addressBus.set(registers.PC);
                    controlBus.MREQ = true;
                    controlBus.RD = true;
                    state = State.DataRead2;
                }
                else if (state == State.DataRead2)
                {
                    tempRegister2 = memoryBus.data;
                    registers.PC++;
                    addressBus.set(registers.PC);
                    controlBus.MREQ = true;
                    controlBus.RD = true;
                    state = State.InstructionExecute;
                    executeInstruction();
                }
                break;
            case 0x3e: //LD   A,n
                if(state == State.InstructionDecode)
                {
                    addressBus.set(registers.PC);
                    controlBus.MREQ = true;
                    controlBus.RD = true;
                    state = State.DataRead;
                }
                else if(state == State.DataRead)
                {
                    tempRegister1 = memoryBus.data;
                    state = State.InstructionExecute;
                    executeInstruction();
                    registers.PC++;
                }
                break;
            case 0x47: //LD   B,A
                state = State.InstructionExecute;
                executeInstruction();
                break;
            case 0x53: // LD  (nn),A
                break;
            case 0x76: // HALT
                state = State.InstructionExecute;
                executeInstruction();
                break;
        }
    }

    private void executeInstruction()
    {
        System.out.println("Executing ir = " + String.format("0x%02X", ir));
        switch(ir)
        {
            case 0x3e:
                registers.A = tempRegister1;
                break;
            case 0x47:
                registers.B = registers.A;
                break;
            case 0x32:
                addressBus.set((128 * tempRegister1) + tempRegister2);
                memoryBus.data = registers.A;
                controlBus.WR = true;
                controlBus.MREQ = true;
                break;
            case 0x76:
                state = State.HALTING;
                return;
        }
        printState();

        controlBus.M1 = true;
        state = State.Idle;
    }

    private void printState()
    {
        System.out.println();
        System.out.println("++++++++++++++++++++++++++");
        System.out.println("registers.PC = " + registers.PC);
        System.out.println("ir = " + String.format("0x%02X", ir));
        System.out.println("registers.A = " + registers.A);
        System.out.println("registers.B = " + registers.B);
        System.out.println("state = " + state);
        System.out.println("--------------------------");
    }

    enum State
    {
        Idle,
        InstructionFetch,
        InstructionRead,
        InstructionDecode,
        DataRead,
        DataRead2,
        InstructionExecute,
        HALTING
    }
}
