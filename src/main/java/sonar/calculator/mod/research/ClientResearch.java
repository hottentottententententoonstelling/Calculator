package sonar.calculator.mod.research;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.research.types.ResearchTypes;

@SideOnly(Side.CLIENT)
public class ClientResearch {

	public static ArrayList<IResearch> research = setResearch(Lists.newArrayList());

	public static ArrayList<IResearch> setResearch(ArrayList<IResearch> research) {
		ArrayList<IResearch> newList = Lists.newArrayList();
		List<IResearch> types = Calculator.research.getObjects();
		for (IResearch type : types) {
			boolean found = false;
			for (IResearch given : research) {
				if (given.getName().equals(type.getName())) {
					newList.add(given);
					found = true;
					break;
				}
			}
			if (!found) {
				newList.add(type.getInstance());
			}
		}
		return newList;
	}

	public static IResearch getSpecificResearch(ResearchTypes researchType) {
		for (IResearch r : research) {
			if (r.getName() == researchType.name()) {
				return r;
			}
		}
		return null;
	}
}
