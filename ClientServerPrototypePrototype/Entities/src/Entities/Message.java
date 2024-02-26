package Entities;

public class Message {
    private OpCodes opcode;
    private Object data;

    public Message(OpCodes Opcode, Object Data) {
        this.opcode = Opcode;
        this.data = Data;
    }

    public Message(OpCodes opcode) {
        this.opcode = opcode;
    }

    public OpCodes GetMsgOpcode() {
        return this.opcode;
    }

    public Object GetMsgData() {
        return this.data;
    }

    public void SetMsgData(Object Data) {
        this.data = Data;
    }

    public void SetMsgOpcodeValue(OpCodes opcode) {
        this.opcode = opcode;
    }
}
