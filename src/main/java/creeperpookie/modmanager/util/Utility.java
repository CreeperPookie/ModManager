package creeperpookie.modmanager.util;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.*;
import java.security.MessageDigest;

public class Utility
{
	public static String formatText(String message)
	{
		StringBuilder newMessage = new StringBuilder();
		String[] itemTypeSegments = message.split("_");
		for (int i = 0; i < itemTypeSegments.length; i++)
		{
			if (itemTypeSegments[i].equalsIgnoreCase("to") || itemTypeSegments[i].equalsIgnoreCase("and") || itemTypeSegments[i].equalsIgnoreCase("or") || itemTypeSegments[i].equalsIgnoreCase("a"))
			{
				itemTypeSegments[i] = itemTypeSegments[i].toLowerCase();
			}
			else
			{
				itemTypeSegments[i] = itemTypeSegments[i].substring(0, 1).toUpperCase() + itemTypeSegments[i].substring(1).toLowerCase();
			}
			newMessage.append(itemTypeSegments[i]);
			if (i < itemTypeSegments.length - 1)
			{
				newMessage.append(" ");
			}
		}
		return newMessage.toString();
	}

	public static <T> boolean arrayContains(T[] array, T value)
	{
		for (T arrayValue : array)
		{
			if (array instanceof String[] && value instanceof String && ((String) arrayValue).equalsIgnoreCase((String) value))
			{
				return true;
			}
			else if (arrayValue.equals(value))
			{
				return true;
			}
		}
		return false;
	}

	public static String locationAsString(BlockPos location)
	{
		return location.getX() + " " + location.getY() + " " + location.getZ();
	}

	public static BlockPos stringAsLocation(String location) // valid formats: "x, y, z"; "x,y,z"; "x y z" |
	{
		if (location == null) return null;
		String[] type1 = location.split(", ");
		if (type1.length != 3)
		{
			String[] type2 = location.split(",");
			if (type2.length != 3)
			{
				String[] type3 = location.split(" ");
				if (type3.length != 3)
				{
					return null;
				}
				return parseBlockPos(type3);
			}
			else
			{
				return parseBlockPos(type2);
			}
		}
		else
		{
			return parseBlockPos(type1);
		}
	}

	@Nullable
	private static BlockPos parseBlockPos(String[] positions)
	{
		if (positions.length != 3) return null;
		int x, y, z;
		try
		{
			x = Integer.parseInt(positions[0]);
			y = Integer.parseInt(positions[1]);
			z = Integer.parseInt(positions[2]);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		return new BlockPos(x, y, z);
	}

	/**
	 * Checks if an array contains all the elements of a second array with the same order.
	 *
	 * @param source the source to check
	 * @param search the array to check against
	 * @return true if the source array contains all the elements of the second array
	 */
	public static <T> boolean arrayContainsAll(T[] source, T[] search)
	{
		if (source.length < search.length) return false;
		int matchCount = 0;
		for (int index = 0; index < source.length; index++)
		{
			T sourceElement = source[index];
			T searchElement = search[matchCount];
			if ((sourceElement == null) != (searchElement == null)) matchCount = 0;
			else if (sourceElement != null && !sourceElement.equals(searchElement)) matchCount = 0;
			if ((source.length - index) < (search.length - matchCount)) break;
			searchElement = search[matchCount];
			if (sourceElement != null && sourceElement.equals(searchElement)) matchCount++;
			if (matchCount == search.length) break;
		}
		return matchCount == search.length;
	}

	/**
	 * Checks if a boolean array contains all the elements of a second boolean array with the same order.
	 *
	 * @param source the source boolean array to check
	 * @param search the boolean array to check against
	 * @return true if the source boolean array contains all the elements of the second boolean array
	 */
	public static boolean arrayContainsAll(boolean[] source, boolean[] search)
	{
		if (source.length < search.length) return false;
		int matchCount = 0;
		for (int index = 0; index < source.length; index++)
		{
			boolean sourceElement = source[index];
			boolean searchElement = search[matchCount];
			if (sourceElement != searchElement)
			{
				matchCount = 0;
				searchElement = search[matchCount];
			}
			if ((source.length - index) < (search.length - matchCount)) break;
			if (sourceElement == searchElement) matchCount++;
			if (matchCount == search.length) break;
		}
		return matchCount == search.length;
	}

	/**
	 * Checks if a byte array contains all the elements of a second byte array with the same order.
	 *
	 * @param source the source byte array to check
	 * @param search the byte array to check against
	 * @return true if the source byte array contains all the elements of the second byte array
	 */
	public static boolean arrayContainsAll(byte[] source, byte[] search)
	{
		if (source.length < search.length) return false;
		int matchCount = 0;
		for (int index = 0; index < source.length; index++)
		{
			byte sourceElement = source[index];
			byte searchElement = search[matchCount];
			if (sourceElement != searchElement)
			{
				matchCount = 0;
				searchElement = search[matchCount];
			}
			if ((source.length - index) < (search.length - matchCount)) break;
			if (sourceElement == searchElement) matchCount++;
			if (matchCount == search.length) break;
		}
		return matchCount == search.length;
	}

	/**
	 * Checks if a char array contains all the elements of a second char array with the same order.
	 *
	 * @param source the source char array to check
	 * @param search the char array to check against
	 * @return true if the source char array contains all the elements of the second char array
	 */
	public static boolean arrayContainsAll(char[] source, char[] search)
	{
		if (source.length < search.length) return false;
		int matchCount = 0;
		for (int index = 0; index < source.length; index++)
		{
			char sourceElement = source[index];
			char searchElement = search[matchCount];
			if (sourceElement != searchElement)
			{
				matchCount = 0;
				searchElement = search[matchCount];
			}
			if ((source.length - index) < (search.length - matchCount)) break;
			if (sourceElement == searchElement) matchCount++;
			if (matchCount == search.length) break;
		}
		return matchCount == search.length;
	}

	/**
	 * Checks if a short array contains all the elements of a second short array with the same order.
	 *
	 * @param source the source short array to check
	 * @param search the short array to check against
	 * @return true if the source short array contains all the elements of the second short array
	 */
	public static boolean arrayContainsAll(short[] source, short[] search)
	{
		if (source.length < search.length) return false;
		int matchCount = 0;
		for (int index = 0; index < source.length; index++)
		{
			short sourceElement = source[index];
			short searchElement = search[matchCount];
			if (sourceElement != searchElement)
			{
				matchCount = 0;
				searchElement = search[matchCount];
			}
			if ((source.length - index) < (search.length - matchCount)) break;
			if (sourceElement == searchElement) matchCount++;
			if (matchCount == search.length) break;
		}
		return matchCount == search.length;
	}

	/**
	 * Checks if an int array contains all the elements of a second int array with the same order.
	 *
	 * @param source the source int array to check
	 * @param search the int array to check against
	 * @return true if the source int array contains all the elements of the second int array
	 */
	public static boolean arrayContainsAll(int[] source, int[] search)
	{
		if (source.length < search.length) return false;
		int matchCount = 0;
		for (int index = 0; index < source.length; index++)
		{
			int sourceElement = source[index];
			int searchElement = search[matchCount];
			if (sourceElement != searchElement)
			{
				matchCount = 0;
				searchElement = search[matchCount];
			}
			if ((source.length - index) < (search.length - matchCount)) break;
			if (sourceElement == searchElement) matchCount++;
			if (matchCount == search.length) break;
		}
		return matchCount == search.length;
	}

	/**
	 * Checks if a long array contains all the elements of a second long array with the same order.
	 *
	 * @param source the source long array to check
	 * @param search the long array to check against
	 * @return true if the source long array contains all the elements of the second long array
	 */
	public static boolean arrayContainsAll(long[] source, long[] search)
	{
		if (source.length < search.length) return false;
		int matchCount = 0;
		for (int index = 0; index < source.length; index++)
		{
			long sourceElement = source[index];
			long searchElement = search[matchCount];
			if (sourceElement != searchElement)
			{
				matchCount = 0;
				searchElement = search[matchCount];
			}
			if ((source.length - index) < (search.length - matchCount)) break;
			if (sourceElement == searchElement) matchCount++;
			if (matchCount == search.length) break;
		}
		return matchCount == search.length;
	}

	/**
	 * Prints the exception to the logger.
	 *
	 * @param logger The logger to print to
	 * @param exception The exception to print
	 */
	public static void printException(Logger logger, Exception exception)
	{
		logger.error(exception.getMessage());
		for (StackTraceElement traceElement : exception.getStackTrace()) logger.error("\tat " + traceElement);

		// Print suppressed exceptions, if any
		for (Throwable se : exception.getSuppressed()) logger.error(se.getMessage());

		// Print cause, if any
		Throwable ourCause = exception.getCause();
		if (ourCause != null) logger.error(exception.getMessage());
	}

	/**
	 * Returns a primitive boolean array from a {@link Boolean Boolean} array.
	 *
	 * @param source the array to source from
	 * @return a boolean[] of all the values from the source
	 */
	public static boolean[] getPrimitiveArray(boolean[] source)
	{
		boolean[] primitive = new boolean[source.length];
		for (int i = 0; i < source.length; i++)
		{
			primitive[i] = source[i];
		}
		return primitive;
	}

	/**
	 * Returns a primitive byte array from a {@link Byte Byte} array.
	 *
	 * @param source the array to source from
	 * @return a byte[] of all the values from the source
	 */
	public static byte[] getPrimitiveArray(Byte[] source)
	{
		byte[] primitive = new byte[source.length];
		for (int i = 0; i < source.length; i++)
		{
			primitive[i] = source[i];
		}
		return primitive;
	}

	/**
	 * Returns a primitive char array from a {@link Character Character} array.
	 *
	 * @param source the array to source from
	 * @return a char[] of all the values from the source
	 */
	public static char[] getPrimitiveArray(Character[] source)
	{
		char[] primitive = new char[source.length];
		for (int i = 0; i < source.length; i++)
		{
			primitive[i] = source[i];
		}
		return primitive;
	}

	/**
	 * Returns a primitive short array from a {@link Short Short} array.
	 *
	 * @param source the array to source from
	 * @return a short[] of all the values from the source
	 */
	public static short[] getPrimitiveArray(Short[] source)
	{
		short[] primitive = new short[source.length];
		for (int i = 0; i < source.length; i++)
		{
			primitive[i] = source[i];
		}
		return primitive;
	}

	/**
	 * Returns a primitive int array from a {@link Integer Integer} array.
	 *
	 * @param source the array to source from
	 * @return a int[] of all the values from the source
	 */
	public static int[] getPrimitiveArray(Integer[] source)
	{
		int[] primitive = new int[source.length];
		for (int i = 0; i < source.length; i++)
		{
			primitive[i] = source[i];
		}
		return primitive;
	}

	/**
	 * Returns a primitive long array from a {@link Long Long} array.
	 *
	 * @param source the array to source from
	 * @return a long[] of all the values from the source
	 */
	public static long[] getPrimitiveArray(Long[] source)
	{
		long[] primitive = new long[source.length];
		for (int i = 0; i < source.length; i++)
		{
			primitive[i] = source[i];
		}
		return primitive;
	}

	/**
	 * Returns the inputted string in reverse order
	 *
	 * @param string the string to reverse
	 * @return the reversed string
	 */
	public static String reverseString(String string)
	{
		return new StringBuilder(string).reverse().toString();
	}

	/**
	 * Swaps the position of characters such index 0 will be at index 1;
	 * Specifically, for an input of "abcd", this will return "badc".
	 * Similarly, swapping an already swapped String would return the original string.
	 * If the inputted string has an odd length, this will throw an IllegalArgumentException.
	 * <br>
	 * @param string the string to swap
	 * @return A string with each pair of characters swapped
	 */
	public static String swapCharacters(String string)
	{
		if (string.length() % 2 != 0) throw new IllegalArgumentException("Strings with an odd length cannot have characters swapped");
		char[] chars = string.toCharArray();
		for (int index = 0; index < chars.length; index += 2)
		{
			char temp = chars[index];
			chars[index] = chars[index + 1];
			chars[index + 1] = temp;
		}
		return new String(chars);
	}

	/**
	 * XOR's a string with a string key.
	 *
	 * @param input the string to XOR against
	 * @param key the key to decode against, repeated if necessary
	 * @return the string output of an XOR on the input string with the key
	 */
	public static String xorString(String input, String key)
	{
		char[] chars = input.toCharArray();
		while (key.length() < input.length()) key += key;
		char[] keyCharArray = key.toCharArray();
		for (int i = 0; i < keyCharArray.length; i++)
		{
			char keyChar = keyCharArray[i];
			chars[i] = (char) ((chars[i] ^ keyChar) & 0xFFFF);
		}
		return new String(chars);
	}

	public static byte[] getFileSHA256(File file, MessageDigest sha256) throws IOException
	{
		byte[] fileBytes = new byte[Math.toIntExact(file.length())];
		DataInputStream fileStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		byte[] buffer;
		int bufferCount = 0;
		while ((buffer = fileStream.readNBytes(Constants.DEFAULT_BUFFER_SIZE)).length > 0)
		{
			System.arraycopy(buffer, 0, fileBytes, bufferCount * Constants.DEFAULT_BUFFER_SIZE, buffer.length);
			bufferCount++;
		}
		byte[] hash = sha256.digest(fileBytes);
		return hash;
	}

	/**
	 * Returns a string representation of a byte array, as a list of hexadecimal numbers.
	 *
	 * @param shouldFormat true if the string should include spacing and formatting
	 * @param bytes          the byte array to print
	 * @return A string representation of the inputted bytes as hexadecimal bytes, as 0x##, 0x##, etc.
	 */
	public static String byteArrayToHex(boolean shouldFormat, byte... bytes)
	{
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < bytes.length; index++)
		{
			if (shouldFormat) builder.append("0x");
			String hex = Integer.toHexString((bytes[index]) & 0xFF);
			if (hex.length() == 1) hex = "0" + hex;
			builder.append(hex.toUpperCase());
			if (shouldFormat && index + 1 < bytes.length) builder.append(", ");
		}
		return builder.toString();
	}
}
