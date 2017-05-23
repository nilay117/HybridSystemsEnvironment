import edu.ucsc.cross.hse.core.component.constructors.Behavior;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.processing.management.Environment;

public class ComponentTester
{

	public static class Beh extends Behavior
	{

		public Data<Double> dat = Data.getDynamicStateElement(2.0);
		public Data<Double> da2t = Data.getDynamicStateElement(2.0);

		@Override
		public boolean jumpSet()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean flowSet()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void flowMap()
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void jumpMap()
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void initialize()
		{
			// TODO Auto-generated method stub

		}

	}

	public static void main(String args[])
	{
		Environment env = new Environment();
		Beh b = new Beh();
		//	b.load();
		Beh c = new Beh();
		b.addComponent(c);
		env.environmentContent().addComponent(b);
		//env.prepareEnvironment();
		env.environmentContent().load();
		b.load();
		System.out.println(b + " " + env.environmentContent().getComponents(Behavior.class, true));
	}
}
