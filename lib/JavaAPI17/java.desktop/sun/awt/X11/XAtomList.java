/*
 * Copyright (c) 2003, 2021, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package sun.awt.X11;

import java.util.*;

/**
 * Helper class to ease the work with the lists of atoms.
 */
class XAtomList {
    Set<XAtom> atoms = new HashSet<XAtom>();

    /**
     * Creates empty list.
     */
    public XAtomList() {
    }

    /**
     * Creates instance of XAtomList and initializes it with
     * the contents pointer by {@code data}.
     * Uses default display to initialize atoms.
     */
    public XAtomList(long data, int count) {
        init(data, count);
    }
    private void init(long data, int count) {
        for (int i = 0; i < count; i++) {
            add(new XAtom(XToolkit.getDisplay(), XAtom.getAtom(data+count*XAtom.getAtomSize())));
        }
    }

    /**
     * Creates instance of XAtomList and initializes it with
     * the arrays of atoms. Array can contain null atoms.
     */
    public XAtomList(XAtom[] atoms) {
        init(atoms);
    }
    private void init(XAtom[] atoms) {
        for (int i = 0; i < atoms.length; i++) {
            add(atoms[i]);
        }
    }

    /**
     * Returns contents of the list as array of atoms.
     */
    public XAtom[] getAtoms() {
        XAtom[] res = new XAtom[size()];
        Iterator<XAtom> iter = atoms.iterator();
        int i = 0;
        while (iter.hasNext()) {
            res[i++] = iter.next();
        }
        return res;
    }

    /**
     * Returns contents of the list as pointer to native data
     * The size of the native data is size of the list multiplied by
     * size of the Atom type on the platform. Caller is responsible for
     * freeing the data by Unsafe.freeMemory when it is no longer needed.
     */
    public long getAtomsData() {
        return XAtom.toData(getAtoms());
    }

    /**
     * Returns true if this list contains the atom {@code atom}
     */
    public boolean contains(XAtom atom) {
        return atoms.contains(atom);
    }

    /**
     * Add atom to the list. Does nothing if list already contains this atom.
     */
    public void add(XAtom atom) {
        atoms.add(atom);
    }

    /**
     * Removes atom from the list. Does nothing if arrays doesn't conaint this atom.
     */
    public void remove(XAtom atom) {
        atoms.remove(atom);
    }


    /**
     * Returns size of the list
     */
    public int size() {
        return atoms.size();
    }

    /**
     * Returns a subset of a list which is intersection of this set and set build by mapping {@code mask} in
     * {@code mapping}.
     */
    public XAtomList subset(int mask, Map<Integer, XAtom> mapping) {
        XAtomList res = new XAtomList();
        Iterator<Integer> iter = mapping.keySet().iterator();
        while (iter.hasNext()) {
            Integer bits = iter.next();
            if ( (mask & bits.intValue()) == bits.intValue() ) {
                XAtom atom = mapping.get(bits);
                if (contains(atom)) {
                    res.add(atom);
                }
            }
        }
        return res;
    }

    /**
     * Returns iterator for items.
     */
    public Iterator<XAtom> iterator() {
        return atoms.iterator();
    }

    /**
     * Merges without duplicates all the atoms from another list
     */
    public void addAll(XAtomList atoms) {
        Iterator<XAtom> iter = atoms.iterator();
        while(iter.hasNext()) {
            add(iter.next());
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        Iterator<XAtom> iter = atoms.iterator();
        while (iter.hasNext()) {
            buf.append(iter.next().toString());
            if (iter.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append("]");
        return buf.toString();
    }
}
