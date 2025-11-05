/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package Tasks;



/**
 *
 * @author apolo
 */
/**
 * Represents a specific integration task,
 * classified by its TaskType.
 */
public enum taskEnum {

    // --- ROUTERS ---
    
    SPLITTER(taskType.ROUTER, "Splits a large message into several smaller ones"),
    ROUTER(taskType.ROUTER, "Sends a message to a destination based on content"),
    DISTRIBUTOR(taskType.DISTRIBUTOR, "distributor si"),

    
    // --- TRANSFORMERS ---
    MERGER(taskType.TRANSFORMER, "Aggregates several small messages into one large one"),
    TRANSLATOR(taskType.TRANSFORMER, "Transforms one data format to another (e.g., XML to JSON)"),
    
    // --- MODIFIERS ---
    ENRICHER(taskType.MODIFIER, "Adds data to the message from an external source"),
    FILTER(taskType.MODIFIER, "Removes parts of the message that do not meet criteria");

    
    private final taskType type;
    private final String description;

    
    taskEnum(taskType type, String description) {
        this.type = type;
        this.description = description;
    }

    
    
    /**
     * Returns the main category of this task.
     * @return The TaskType (ROUTER, TRANSFORMER, or MODIFIER)
     */
    public taskType getType() {
        return type;
    }
    
    /**
     * Returns the description of what the task does.
     * @return A String with the description
     */
    public String getDescription() {
        return description;
    }
    
    // Convenience method for quick checks
    public boolean isRouter() {
        return this.type == taskType.ROUTER;
    }
    
    public boolean isTransformer() {
        return this.type == taskType.TRANSFORMER;
    }
    
    public boolean isModifier() {
        return this.type == taskType.MODIFIER;
    }
}
