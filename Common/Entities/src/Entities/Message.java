package Entities;

import java.io.Serializable;

/**
 * Represents a message sent between system components.
 */
public class Message implements Serializable {
    private OpCodes opcode;
    private String identifier;
    private Object data;

    /**
     * Constructs a new Message object with the specified opcode, username, and data.
     *
     * @param opcode   The operation code indicating the type of message.
     * @param identifier The username associated with the message.
     * @param data     The data contained within the message.
     */
    public Message(OpCodes opcode, String identifier, Object data) {
        this.identifier = identifier;
        this.opcode = opcode;
        this.data = data;
    }

    /**
     * Constructs a new Message object with the specified opcode.
     *
     * @param opcode The operation code indicating the type of message.
     */
    public Message(OpCodes opcode) {
        this.opcode = opcode;
    }

    /**
     * Retrieves the operation code of the message.
     *
     * @return The operation code.
     */
    public OpCodes getMsgOpcode() {
        return this.opcode;
    }

    /**
     * Retrieves the data contained within the message.
     *
     * @return The data of the message.
     */
    public Object getMsgData() {
        return this.data;
    }

    /**
     * Retrieves the username associated with the message.
     *
     * @return The username of the message.
     */
    public String getMsgUserName() {
        return this.identifier;
    }

    /**
     * Sets the data contained within the message.
     *
     * @param data The data to be set.
     */
    public void setMsgData(Object data) {
        this.data = data;
    }

    /**
     * Sets the operation code of the message.
     *
     * @param opcode The operation code to be set.
     */
    public void setMsgOpcodeValue(OpCodes opcode) {
        this.opcode = opcode;
    }

    /**
     * Sets the username associated with the message.
     *
     * @param identifier The username to be set.
     */
    public void setMsgUserName(String identifier) {
        this.identifier = identifier;
    }
}
