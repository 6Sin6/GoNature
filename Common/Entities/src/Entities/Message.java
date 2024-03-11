package Entities;

import java.io.Serializable;

/**
 * Represents a message sent between system components.
 */
public class Message implements Serializable {
    private OpCodes opcode;
    private String username;
    private Object data;

    /**
     * Constructs a new Message object with the specified opcode, username, and data.
     *
     * @param opcode   The operation code indicating the type of message.
     * @param username The username associated with the message.
     * @param data     The data contained within the message.
     */
    public Message(OpCodes opcode, String username, Object data) {
        this.username = username;
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
        return this.username;
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
     * @param username The username to be set.
     */
    public void setMsgUserName(String username) {
        this.username = username;
    }
}
