package co.uk.dreadpirateroberts.z80;

import java.util.Observable;
import java.util.Observer;

public class InstructionDecoder implements Observer {
    private Registers registers;
    private AddressBus addressBus;
    private ControlBus controlBus;
    private MemoryBus memoryBus;
    private State state = State.Idle;
    private byte ir = 0;
    private byte tempRegister1 = 0;

    public InstructionDecoder(Registers r, AddressBus ab, ControlBus cb, MemoryBus mb)
    {
        registers = r;
        addressBus = ab;
        controlBus = cb;
        memoryBus = mb;
    }

    @Override
    public void update(Observable o, Object arg) {
        printState();
        if (controlBus.M1) {
            state = State.InstructionFetch;
            fetchInstruction();
            controlBus.M1 = false;
        }
        else
        {
            switch(state)
            {
                case InstructionFetch:
                    state = State.InstructionRead;
                    readInstruction();
                    break;
                case InstructionRead:
                    state = State.InstructionDecode;
                    decodeInstruction();
                    break;
                case DataRead:
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
            case 62:
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
            case 71:
                state = State.InstructionExecute;
                executeInstruction();
                break;
            case 118:
                state = State.InstructionExecute;
                executeInstruction();
                break;
        }
    }

    private void executeInstruction()
    {
        System.out.println("Executing ir = " + ir);
        switch(ir)
        {
            case 62:
                registers.A = tempRegister1;
                break;
            case 71:
                registers.B = registers.A;
                break;
            case 118:
                System.exit(0);
        }
        printState();

        controlBus.M1 = true;
        state = State.Idle;
    }

    private void printState()
    {
        System.out.println("++++++++++++++++++++++++++");
        System.out.println("registers.PC = " + registers.PC);
        System.out.println("ir = " + ir);
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
        InstructionExecute
    }
}
