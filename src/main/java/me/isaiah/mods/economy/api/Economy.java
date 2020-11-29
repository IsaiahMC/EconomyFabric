package me.isaiah.mods.economy.api;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import me.isaiah.mods.economy.FabricEconomyMod;
import static me.isaiah.mods.economy.FabricEconomyMod.PROVIDER;


public class Economy {

    private static final Logger logger = FabricEconomyMod.LOGGER;
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    public static EconomyUser getUser(String name) throws UserDoesNotExistException {
        return PROVIDER.getUser(name);
    }

    /**
     * Returns the balance of a user
     *
     * @param name Name of the user
     * @return balance
     */
    @Deprecated
    public static double getMoney(String name) throws UserDoesNotExistException {
        return PROVIDER.getMoneyExact(name).doubleValue();
    }

    public static BigDecimal getMoneyExact(String name) throws UserDoesNotExistException {
        return PROVIDER.getMoneyExact(name);
    }

    /**
     * Sets the balance of a user
     *
     * @param name    Name of the user
     * @param balance The balance you want to set
     *
     * @throws Exception If user by name does not exist or not allowed negative balance
     */
    @Deprecated
    public static void setMoney(String name, double balance) throws UserDoesNotExistException, NoLoanPermittedException {
        PROVIDER.setMoney(name, BigDecimal.valueOf(balance));
    }

    public static void setMoney(String name, BigDecimal balance) throws UserDoesNotExistException, NoLoanPermittedException {
        PROVIDER.setMoney(name, balance);
    }

    /**
     * Adds money to the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to add
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void add(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            PROVIDER.add(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to add " + amount + " to balance of " + name + ": " + e.getMessage(), e);
        }
    }

    public static void add(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException {
        setMoney(name, getMoneyExact(name).add(amount, MATH_CONTEXT));
    }

    /**
     * Subtracts money from the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to subtract
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void subtract(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            PROVIDER.substract(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN,
                    "Failed to substract " + amount + " of balance of " + name + ": " + e.getMessage(), e);
        }
    }

    public static void substract(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException {
        PROVIDER.substract(name, amount);
    }

    /**
     * Divides the balance of a user by a value
     *
     * @param name  Name of the user
     * @param value The balance is divided by this value
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    @Deprecated
    public static void divide(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            PROVIDER.divide(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to divide balance of " + name + " by " + amount + ": " + e.getMessage(), e);
        }
    }

    public static void divide(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException {
        PROVIDER.divide(name, amount);
    }

    /**
     * Multiplies the balance of a user by a value
     *
     * @param name  Name of the user
     * @param value The balance is multiplied by this value
     */
    @Deprecated
    public static void multiply(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            PROVIDER.multiply(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to multiply balance of " + name + " by " + amount + ": " + e.getMessage(), e);
        }
    }

    public static void multiply(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException {
        PROVIDER.setMoney(name, getMoneyExact(name).multiply(amount, MATH_CONTEXT));
    }

    /**
     * Resets the balance of a user to the starting balance
     *
     * @param name Name of the user
     * @throws Exception If user by name does not exist or not allowed to have a negative balance
     */
    public static void resetBalance(String name) throws UserDoesNotExistException, NoLoanPermittedException {
        PROVIDER.resetBalance(name);
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     *
     * @return true, if the user has more or an equal amount of money
     */
    public static boolean hasEnough(String name, double amount) throws UserDoesNotExistException {
        try {
            return PROVIDER.hasEnough(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean hasEnough(String name, BigDecimal amount) throws UserDoesNotExistException {
        return PROVIDER.hasEnough(name, amount);
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     *
     * @return true, if the user has more money
     */
    public static boolean hasMore(String name, double amount) throws UserDoesNotExistException {
        try {
            return PROVIDER.hasMore(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.warn("Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean hasMore(String name, BigDecimal amount) throws UserDoesNotExistException {
        return PROVIDER.hasMore(name, amount);
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should not have
     *
     * @return true, if the user has less money
     */
    public static boolean hasLess(String name, double amount) throws UserDoesNotExistException {
        try {
            return PROVIDER.hasLess(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean hasLess(String name, BigDecimal amount) throws UserDoesNotExistException {
        return PROVIDER.hasLess(name, amount);
    }

    /**
     * Test if the user has a negative balance
     *
     * @param name Name of the user
     * @return true, if the user has a negative balance
     */
    public static boolean isNegative(String name) throws UserDoesNotExistException {
        return PROVIDER.isNegative(name);
    }

    @Deprecated
    public static String format(double amount) {
        try {
            return PROVIDER.format(BigDecimal.valueOf(amount));
        } catch (NumberFormatException e) {
            logger.log(Level.WARN, "Failed to display " + amount + ": " + e.getMessage(), e);
            return "NaN";
        }
    }

    public static String format(BigDecimal amount) {
        return PROVIDER.format(amount);
    }

    /**
     * Test if a player exists
     *
     * @param name Name of the user
     * @return true, if the user exists
     */
    public static boolean playerExists(String name) {
        return PROVIDER.playerExists(name);
    }

    public static boolean isNPC(String name) throws Exception {
        return PROVIDER.isNPC(name);
    }

    public static boolean createNPC(String name) {
        return PROVIDER.createNPC(name);
    }

    public static boolean removeNPC(String name) {
        return PROVIDER.removeNPC(name);
    }

}