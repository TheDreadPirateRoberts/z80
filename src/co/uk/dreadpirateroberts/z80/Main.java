package co.uk.dreadpirateroberts.z80;

public class Main {

    public static void main(String[] args) {
        Registers registers = new Registers();
        AddressBus addressBus = new AddressBus();
        ControlBus controlBus = new ControlBus();
        MemoryBus memoryBus = new MemoryBus();
        controlBus.M1 = true;

        Clock clock = new Clock();
        InstructionDecoder ID =
                new InstructionDecoder(registers,
                        addressBus,
                        controlBus,
                        memoryBus);

        clock.addObserver(ID);

        MemoryController memoryController =
                new MemoryController(addressBus,
                        memoryBus,
                        controlBus);

        clock.addObserver(memoryController);

        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
