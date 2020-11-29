package me.isaiah.mods.economy.api;

import static me.isaiah.mods.economy.FabricEconomyMod.MINECRAFT_SERVER;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import me.isaiah.mods.economy.FabricEconomyMod;

/**
 * The Default Economy Provider
 */
public class DefaultEconomyProvider implements IEconomyProvider {

    private final Logger logger = FabricEconomyMod.LOGGER;
    public final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    public EconomyUser getUser(String name) throws UserDoesNotExistException {
        EconomyUser user = (EconomyUser) MINECRAFT_SERVER.getPlayerManager().getPlayer(name);
        if (null != user)
            return user;

        if (!FabricEconomyMod.offlineMap.containsKey(name))
            throw new UserDoesNotExistException(name);

        return FabricEconomyMod.offlineMap.getOrDefault(name, null);
    }

    /**
     * Returns the balance of a user
     *
     * @param name Name of the user
     * @return balance
     */
    @Deprecated
    public double getMoney(String name) throws UserDoesNotExistException {
        return getMoneyExact(name).doubleValue();
    }

    public BigDecimal getMoneyExact(String name) throws UserDoesNotExistException {
        EconomyUser user = getUser(name);
        if (user == null) throw new UserDoesNotExistException("User does not exist: " + name);

        return user.getMoney();
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
    public void setMoney(String name, double balance) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            setMoney(name, BigDecimal.valueOf(balance));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to set balance of " + name + " to " + balance + ": " + e.getMessage(), e);
        }
    }

    public void setMoney(String name, BigDecimal balance) throws UserDoesNotExistException, NoLoanPermittedException {
        EconomyUser user = getUser(name);

        if (user == null) throw new UserDoesNotExistException(name);
        if (balance.compareTo(new BigDecimal(0)) < 0) throw new NoLoanPermittedException();

        if (balance.signum() < 0)
            throw new NoLoanPermittedException();

        try {
            user.setMoney(balance);
        } catch (Exception ex) {
            ex.printStackTrace();
            // TODO: Update API to show max balance errors
        }
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
    public void add(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            add(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to add " + amount + " to balance of " + name + ": " + e.getMessage(), e);
        }
    }

    public void add(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException {
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
    public void subtract(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            substract(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN,
                    "Failed to substract " + amount + " of balance of " + name + ": " + e.getMessage(), e);
        }
    }

    public void substract(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException {
        setMoney(name, getMoneyExact(name).subtract(amount, MATH_CONTEXT));
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
    public void divide(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            divide(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to divide balance of " + name + " by " + amount + ": " + e.getMessage(), e);
        }
    }

    public void divide(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException {
        setMoney(name, getMoneyExact(name).divide(amount, MATH_CONTEXT));
    }

    /**
     * Multiplies the balance of a user by a value
     *
     * @param name  Name of the user
     * @param value The balance is multiplied by this value
     */
    @Deprecated
    public void multiply(String name, double amount) throws UserDoesNotExistException, NoLoanPermittedException {
        try {
            multiply(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to multiply balance of " + name + " by " + amount + ": " + e.getMessage(), e);
        }
    }

    public void multiply(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException {
        setMoney(name, getMoneyExact(name).multiply(amount, MATH_CONTEXT));
    }

    /**
     * Resets the balance of a user to the starting balance
     *
     * @param name Name of the user
     * @throws Exception If user by name does not exist or not allowed to have a negative balance
     */
    public void resetBalance(String name) throws UserDoesNotExistException, NoLoanPermittedException {
        if (!FabricEconomyMod.LOADED)
            throw new RuntimeException("API is called before FabricEconomy is loaded.");

        setMoney(name, 100); // TODO: configure
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     *
     * @return true, if the user has more or an equal amount of money
     */
    public boolean hasEnough(String name, double amount) throws UserDoesNotExistException {
        try {
            return hasEnough(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    public boolean hasEnough(String name, BigDecimal amount) throws UserDoesNotExistException {
        return amount.compareTo(getMoneyExact(name)) <= 0;
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     *
     * @return true, if the user has more money
     */
    public boolean hasMore(String name, double amount) throws UserDoesNotExistException {
        try {
            return hasMore(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.warn("Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage());
            return false;
        }
    }

    public boolean hasMore(String name, BigDecimal amount) throws UserDoesNotExistException {
        return amount.compareTo(getMoneyExact(name)) < 0;
    }

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should not have
     *
     * @return true, if the user has less money
     */
    public boolean hasLess(String name, double amount) throws UserDoesNotExistException {
        try {
            return hasLess(name, BigDecimal.valueOf(amount));
        } catch (ArithmeticException e) {
            logger.log(Level.WARN, "Failed to compare balance of " + name + " with " + amount + ": " + e.getMessage(), e);
            return false;
        }
    }

    public boolean hasLess(String name, BigDecimal amount) throws UserDoesNotExistException {
        return amount.compareTo(getMoneyExact(name)) > 0;
    }

    /**
     * Test if the user has a negative balance
     *
     * @param name Name of the user
     * @return true, if the user has a negative balance
     */
    public boolean isNegative(String name) throws UserDoesNotExistException {
        return getMoneyExact(name).signum() < 0;
    }

    @Deprecated
    public String format(double amount) {
        try {
            return format(BigDecimal.valueOf(amount));
        } catch (NumberFormatException e) {
            logger.log(Level.WARN, "Failed to display " + amount + ": " + e.getMessage(), e);
            return "NaN";
        }
    }

    public String format(BigDecimal amount) {
        return "$" + amount.floatValue();
    }

    /**
     * Test if a player exists
     *
     * @param name Name of the user
     * @return true, if the user exists
     */
    public boolean playerExists(String name) {
        return null != ((EconomyUser) MINECRAFT_SERVER.getPlayerManager().getPlayer(name));
    }

    public boolean isNPC(String name) throws Exception {
        return false;
    }

    public boolean createNPC(String name) {
        return false;
    }

    public boolean removeNPC(String name) {
        return false;
    }


}