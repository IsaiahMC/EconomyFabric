package me.isaiah.mods.economy.api;

import java.math.BigDecimal;

/**
 * Interface that has full control over the Economy
 */
public interface IEconomyProvider {

    public EconomyUser getUser(String name) throws UserDoesNotExistException;

    /**
     * Returns the balance of a user
     *
     * @param name Name of the user
     * @return balance
     */
    public BigDecimal getMoneyExact(String name) throws UserDoesNotExistException;

    /**
     * Sets the balance of a user
     *
     * @param name    Name of the user
     * @param balance The balance you want to set
     *
     * @throws Exception If user by name does not exist or not allowed negative balance
     */
    public void setMoney(String name, BigDecimal balance) throws UserDoesNotExistException, NoLoanPermittedException;

    /**
     * Adds money to the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to add
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public void add(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException;

    /**
     * Subtracts money from the balance of a user
     *
     * @param name   Name of the user
     * @param amount The money you want to subtract
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public void substract(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException;

    /**
     * Divides the balance of a user by a value
     *
     * @param name  Name of the user
     * @param value The balance is divided by this value
     *
     * @throws UserDoesNotExistException If a user by that name does not exists
     * @throws NoLoanPermittedException  If the user is not allowed to have a negative balance
     */
    public void divide(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException;

    /**
     * Multiplies the balance of a user by a value
     *
     * @param name  Name of the user
     * @param value The balance is multiplied by this value
     */
    public void multiply(String name, BigDecimal amount) throws UserDoesNotExistException, NoLoanPermittedException;

    /**
     * Resets the balance of a user to the starting balance
     *
     * @param name Name of the user
     * @throws Exception If user by name does not exist or not allowed to have a negative balance
     */
    public void resetBalance(String name) throws UserDoesNotExistException, NoLoanPermittedException;

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     *
     * @return true, if the user has more or an equal amount of money
     */
    public boolean hasEnough(String name, BigDecimal amount) throws UserDoesNotExistException;

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should have
     *
     * @return true, if the user has more money
     */
    public boolean hasMore(String name, BigDecimal amount) throws UserDoesNotExistException;

    /**
     * @param name   Name of the user
     * @param amount The amount of money the user should not have
     *
     * @return true, if the user has less money
     */
    public boolean hasLess(String name, BigDecimal amount) throws UserDoesNotExistException;

    /**
     * Test if the user has a negative balance
     *
     * @param name Name of the user
     * @return true, if the user has a negative balance
     */
    public boolean isNegative(String name) throws UserDoesNotExistException;

    /**
     * Formats a balance String
     * Ex. $100
     */
    public String format(BigDecimal amount);

    /**
     * Test if a player exists
     *
     * @param name Name of the user
     * @return true, if the user exists
     */
    public boolean playerExists(String name);

    public boolean isNPC(String name) throws Exception;

    public boolean createNPC(String name);

    public boolean removeNPC(String name);

}