package pama1234.boxmatica.materials;

import java.util.List;
import pama1234.boxmatica.util.BlockInfoListType;

public interface IMaterialList{
  BlockInfoListType getMaterialListType();

  void setMaterialListEntries(List<MaterialListEntry> list);
}
