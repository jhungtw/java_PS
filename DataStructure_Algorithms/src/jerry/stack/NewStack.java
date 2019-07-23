package jerry.stack;

import java.util.LinkedList;

public class NewStack implements I_NewStack {
	private LinkedList<Integer> _linkedList = new LinkedList<Integer>();

	@Override
	public void AddNew(Integer element) {
		// TODO Auto-generated method stub
		_linkedList.add(element);
	}

	@Override
	public int Length() {
		// TODO Auto-generated method stub
		return _linkedList.size();
	}

}
