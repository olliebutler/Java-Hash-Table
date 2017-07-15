
//The complexity of get and put method on average is O(1) and worst case are O(N)
//As the load factor increases the complexity also increases as there is more chance that you will
//already have an element with the same keyhash.
//Worst case is O(N) because it may have to resize and resizing the hashtable is a O(N) operation
//As the load factor increases the complexity increases too, as there is more chance that
//You will already have an element with the same hash and will have to find an empty location


package ci284.ass2.htable;

/**
 * A HashTable with no deletions allowed. Duplicates overwrite the existing value. Values are of
 * type V and keys are strings -- one extension is to adapt this class to use other types as keys.
 * 
 * The underlying data is stored in the array `arr', and the actual values stored are pairs of 
 * (key, value). This is so that we can detect collisions in the hash function and look for the next 
 * location when necessary.
 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

public class Hashtable<V> {

	private Object[] arr; //an array of Pair objects, where each pair contains the key and value stored in the hashtable
	private int max; //the size of arr. This should be a prime number
	private int itemCount; //the number of items stored in arr
	private final double maxLoad = 0.6; //the maximum load factor

	public static enum PROBE_TYPE {
		LINEAR_PROBE, QUADRATIC_PROBE, DOUBLE_HASH;
	}

	PROBE_TYPE probeType; //the type of probe to use when dealing with collisions
	private final BigInteger DBL_HASH_K = BigInteger.valueOf(8);

	/**
	 * Create a new Hashtable with a given initial capacity and using a given probe type
	 * @param initialCapacity
	 * @param pt
	 */
	public Hashtable(int initialCapacity, PROBE_TYPE pt) {
		
		itemCount = 0;
		
		if(isPrime(initialCapacity) == true) max = initialCapacity;
		else max = nextPrime(initialCapacity);
		
		arr = new Object[max];
		
		probeType = pt;
		
	}
	
	/**
	 * Create a new Hashtable with a given initial capacity and using the default probe type
	 * @param initialCapacity
	 */
	public Hashtable(int initialCapacity) {
		
		itemCount = 0;
		
		if(isPrime(initialCapacity) == true) max = initialCapacity;
		else max = nextPrime(initialCapacity);
		
		arr = new Object[max];
		
		probeType = PROBE_TYPE.LINEAR_PROBE;
	}

	/**
	 * Store the value against the given key. If the loadFactor exceeds maxLoad, call the resize 
	 * method to resize the array. the If key already exists then its value should be overwritten.
	 * Create a new Pair item containing the key and value, then use the findEmpty method to find an unoccupied 
	 * position in the array to store the pair. Call findEmmpty with the hashed value of the key as the starting
	 * position for the search, stepNum of zero and the original key.
	 * containing   
	 * @param key
	 * @param value
	 */
	public void put(String key, V value) {

		
		if(getLoadFactor() > maxLoad){
			
			resize(max*2);
		}
		
		Hashtable<V>.Pair pair = new Pair(key, value);
		
		int hashedKey = hash(key);
		
		int empty = findEmpty(hashedKey, 0, key);
		
		arr[empty] = pair;
		
		itemCount++;
			
	}

	/**
	 * Get the value associated with key, or return null if key does not exists. Use the find method to search the
	 * array, starting at the hashed value of the key, stepNum of zero and the original key.
	 * @param key
	 * @return
	 */
	public V get(String key) {
		
		return find(hash(key), key, 0);	
		
	}

	/**
	 * Return true if the Hashtable contains this key, false otherwise 
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		
		int hashedKey = hash(key);
		
		if(arr[hashedKey] == null){
			return false;
		}
		return true;
	
	}

	/**
	 * Return all the keys in this Hashtable as a collection
	 * @return
	 */
	public Collection<String> getKeys() {
		
		
		ArrayList<String> keys = new ArrayList<>();
		
		for(int i = 0; i < arr.length; i++){
			if(arr[i] != null){
				Hashtable<V>.Pair pair = (Pair) arr[i];
				String key = pair.returnKey();
				keys.add(key);
			}
		}
		return keys;
		
	}

	/**
	 * Return the load factor, which is the ratio of itemCount to max
	 * @return
	 */
	public double getLoadFactor() {
		
		double loadFactor = 0;
		loadFactor = itemCount / (double)max;
		return loadFactor;
	}


	
	
	
	
	/**
	 * return the maximum capacity of the Hashtable
	 * @return
	 */
	public int getCapacity() {
		return max;
	}
	
	/**
	 * Find the value stored for this key, starting the search at position startPos in the array. If
	 * the item at position startPos is null, the Hashtable does not contain the value, so return null. 
	 * If the key stored in the pair at position startPos matches the key we're looking for, return the associated 
	 * value. If the key stored in the pair at position startPos does not match the key we're looking for, this
	 * is a hash collision so use the getNextLocation method with an incremented value of stepNum to find 
	 * the next location to search (the way that this is calculated will differ depending on the probe type 
	 * being used). Then use the value of the next location in a recursive call to find.
	 * @param startPos
	 * @param key
	 * @param stepNum
	 * @return
	 */
	private V find(int startPos, String key, int stepNum) {
		
		if(arr[startPos] == null) return null;
		
		Hashtable<V>.Pair pair = (Pair) arr[startPos];
		
		String keyToTest = pair.returnKey();
		V value = pair.returnValue();
		
		if(key.equals(keyToTest)) return value;
		
		stepNum = stepNum + 1;
		
		return find(getNextLocation(startPos, stepNum, key), key, stepNum);
		
		
	}

	/**
	 * Find the first unoccupied location where a value associated with key can be stored, starting the
	 * search at position startPos. If startPos is unoccupied, return startPos. Otherwise use the getNextLocation
	 * method with an incremented value of stepNum to find the appropriate next position to check 
	 * (which will differ depending on the probe type being used) and use this in a recursive call to findEmpty.
	 * @param startPos
	 * @param stepNum
	 * @param key
	 * @return
	 */
	private int findEmpty(int startPos, int stepNum, String key) {

		
		if(arr[startPos] == null){
			return startPos;
		}
		
		Hashtable<V>.Pair pair = (Pair) arr[startPos];
		String keyToTest = pair.returnKey();

		
		if(key.equals(keyToTest)) return startPos;

		
		stepNum = stepNum + 1;
		int next = getNextLocation(startPos, stepNum, key);
		
		return findEmpty(next, stepNum, key);
			
	}

	/**
	 * Finds the next position in the Hashtable array starting at position startPos. If the linear
	 * probe is being used, we just increment startPos. If the double hash probe type is being used, 
	 * add the double hashed value of the key to startPos. If the quadratic probe is being used, add
	 * the square of the step number to startPos.
	 * @param i
	 * @param stepNum
	 * @param key
	 * @return
	 */
	private int getNextLocation(int startPos, int stepNum, String key) {
		int step = startPos;
		switch (probeType) {
		case LINEAR_PROBE:
			step++;
			break;
		case DOUBLE_HASH:
			step += doubleHash(key);
			break;
		case QUADRATIC_PROBE:
			step += stepNum * stepNum;
			break;
		default:
			break;
		}
		return step % max;
	}

	/**
	 * A secondary hash function which returns a small value (less than or equal to DBL_HASH_K)
	 * to probe the next location if the double hash probe type is being used
	 * @param key
	 * @return
	 */
	private int doubleHash(String key) {
		BigInteger hashVal = BigInteger.valueOf(key.charAt(0) - 96);
		for (int i = 1; i < key.length(); i++) {
			BigInteger c = BigInteger.valueOf(key.charAt(i) - 96);
			hashVal = hashVal.multiply(BigInteger.valueOf(27)).add(c);
		}
		return DBL_HASH_K.subtract(hashVal.mod(DBL_HASH_K)).intValue();
	}

	/**
	 * Return an int value calculated by hashing the key. See the lecture slides for information
	 * on creating hash functions. The return value should be less than max, the maximum capacity 
	 * of the array
	 * @param key
	 * @return
	 */
	private int hash(String key) {

		
		int hashVal = key.charAt(0) - 31;
		
		for(int i = 0; i < key.length(); i++){
			int c = key.charAt(i) - 31;
			hashVal = (hashVal * 93 + c) % (max);
			hashVal = hashVal % (max);
		}
		
		return hashVal;
		
	}

	/**
	 * Return true if n is prime
	 * @param n
	 * @return
	 */
	private boolean isPrime(int n) {
		
		if(n == 2) return true;
		if(n % 2 == 0) return false;
		
		for (int i = 3; i * i <= n; i += 2){
			if( n % i== 0) return false;	
		}
		return true;
	}

	/**
	 * Get the smallest prime number which is larger than n
	 * @param n
	 * @return
	 */
	private int nextPrime(int n) {
		
		for(int i = n; true; i++){
			if(isPrime(i)){
				return i;
			}
		}
	}

	/**
	 * Resize the hashtable, to be used when the load factor exceeds maxLoad. The new size of
	 * the underlying array should be the smallest prime number which is at least twice the size
	 * of the old array.
	 */
	private void resize(int newMax) {
		
		Object[] tempArr = arr.clone();
		
		arr = new Object[newMax];
		
		max = newMax;
		
		
		
		itemCount = 0;
		
		for(int i = 0; i < tempArr.length; i++){
			
			if(tempArr[i] != null){
				
				Hashtable<V>.Pair tempPair = (Pair) tempArr[i];
	
				String key = tempPair.returnKey();
				V value = tempPair.returnValue();
				
				put(key, value);
			}
		}

	}

	
	/**
	 * Instances of Pair are stored in the underlying array. We can't just store
	 * the value because we need to check the original key in the case of collisions.
	 * @author jb259
	 *
	 */
	private class Pair {
		private String key;
		private Object value;

		public Pair(String key, Object value) {
			this.key = key;
			this.value = value;
		}
		public V returnValue(){
			return (V) this.value;
		}
		public String returnKey(){
			return this.key;
		}
	}

}
